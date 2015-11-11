import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class CFClient extends Thread {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static boolean macroBusy;

    private String name = "";
    private String ip;
    private int keyPort;
    private int mousePort;
    private Socket clientSocket;
    private boolean isRunning;
    private boolean ready = false;

    private byte[] recvBuf;
    private byte[] msgBuffer;
    private BufferedInputStream is;
    private BufferedOutputStream os;
    private String receivedMsg;
    private String returnMsg;
    private boolean isSuspended;

    private MainFrame mainFrame;
    private MacroRecorder mr;
    private CFService server;

    private Timer timer;


    public CFClient(Socket socket, String clientIP, MainFrame mf, CFService server, int keyPort, int mousePort) {
        this.isRunning = true;
        this.clientSocket = socket;
        this.ip = clientIP;
        this.mainFrame = mf;
        this.server = server;
        this.isSuspended = false;
        this.keyPort = keyPort;
        this.mousePort = mousePort;
    }

    public String getClientName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "{ ClientName: " + name + "\tClientIP: " + ip + " Suspended: " + isSuspended + " }";
    }

    @Override
    public int hashCode() {
        return clientSocket.hashCode() + ip.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.ip.equals(((CFClient) obj).ip);

    }

    public void run() {

        try {
            is = new BufferedInputStream(clientSocket.getInputStream(), 512);
            os = new BufferedOutputStream(clientSocket.getOutputStream(), 512);


            while (isRunning) {
                this.recvBuf = new byte[512];
                if (is.read(recvBuf) > 0 && isRunning) {
                    receivedMsg = new String(recvBuf);

                    receivedMsg = receivedMsg.trim();
                    LOGGER.info("The message: " + receivedMsg);

                    String[] splitedMsg = receivedMsg.split(":");

                    LOGGER.info(splitedMsg[0]);


                    switch (splitedMsg[0]) {

                        case "ControlyClient":
                            this.name = splitedMsg[1];
                            new Thread(new NotificationFrame(this.name, 0)).start();
                            mainFrame.addClientToLabel(this);
                            returnMsg = "1000-OK:" + keyPort + ":" + mousePort;
                            msgBuffer = returnMsg.getBytes();
                            os.write(msgBuffer);
                            os.flush();
                            LOGGER.info("Received connection request from client: " + this.name + " address: " + this.ip);
                            server.printClients();
                            ready = true;
                            break;

                        case "MacroStart":
                            LOGGER.info(macroBusy ? "macro busy" : " macro free");
                            if (!macroBusy) {
                                macroBusy = true;
                                returnMsg = "2000-macro record started";
                                msgBuffer = returnMsg.getBytes();
                                os.write(msgBuffer);
                                os.flush();
                                LOGGER.info("Received Macro Start Msg");

                                if (Integer.parseInt(splitedMsg[1]) == 0)
                                    mr = new MacroRecorder(false, this.ip);
                                else
                                    mr = new MacroRecorder(true, this.ip);
                                mr.start();
                                new Thread(new NotificationFrame("", 1)).start();
                            } else {
                                returnMsg = "2002-cannot record more then one macro at a time";
                                msgBuffer = returnMsg.getBytes();
                                os.write(msgBuffer);
                                os.flush();
                            }
                            break;

                        case "MacroStop":
                            LOGGER.info("Received Macro Stop Msg");
                            mr.stopRecord();
                            new Thread(new NotificationFrame("", 2)).start();
                            returnMsg = mr.buildMacro();
                            if (returnMsg == "")
                                returnMsg = "2001-Empty";
                            else
                                returnMsg = "2001-" + returnMsg;
                            mr.finishMacro();
                            msgBuffer = returnMsg.getBytes();
                            os.write(msgBuffer);
                            os.flush();
                            LOGGER.info(returnMsg);
                            macroBusy = false;
                            break;

                        case "Disconnect":
                            closeClient();
                            LOGGER.info("Client Disconnected  " + this);
                            break;

                        case "Suspend":
                            if (timer != null)
                                timer.cancel();
                            isSuspended = true;
                            LOGGER.info("Client Suspended " + this);
                            break;

                        case "UnSuspend":
                            isSuspended = false;
                            LOGGER.info("Client UnSuspend " + this);
                            break;

                        case "Pong":
                            if (timer != null)
                                timer.cancel();
                            LOGGER.info("Received ping back from client: " + this);
                            break;

                        default:
                            LOGGER.warning("Received Wrong Message from client: " + this);
                            break;

                    }
                }

            }


        } catch (IOException e) {
            LOGGER.info("IO Exception occurred - Ignore this if it happened after ping TimeOut - " + e.getMessage() + " From Client: " + this.toString());

        } finally {
            try {
                if (!clientSocket.isClosed())
                    this.clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.removeClient(this);
            server.printClients();
        }
    }

    public void closeClient() {
        isRunning = false;
    }

    public void pingClient() {
        if (ready && !isSuspended) {
            returnMsg = "Ping";
            msgBuffer = returnMsg.getBytes();
            try {
                os.write(msgBuffer);
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            LOGGER.info("Pinging client: " + name + " ip: " + ip);

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LOGGER.info("TimeOut - not received ping back from client: " + name + " ip: " + ip);
                    closeClient();
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 30000);
        }

    }

}
