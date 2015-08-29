

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CFService extends Thread {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private HashSet<CFClient> clients;
    private ServerSocket serverSocket;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private DatagramChannel mouseDatagramChannel;
    private DatagramChannel keysDatagramChannel;
    private CFKeysDatagramChannel keysChannel;
    private CFMouseDatagramChannel mouseChannel;
    private CFMessagesReceiver messagesReceiver;
    private BCListener bcListener;
    private boolean isRuning;
    private String receivedMsg;
    private MainFrame mainFrame;
    private String myIp;
    private MacroRecorder mr;
    private boolean macroBusy;

    public CFService(MainFrame mf) throws IOException {
        mainFrame = mf;
        macroBusy = false;
        clients = new HashSet<>();
        //tweenManager = manager;
        //should create new exception object to deal with specifiec errors.
        serverSocket = new ServerSocket(0);
        //0 means choose an available port.
        // messagesReceiver = new CFMessagesReceiver();
        isRuning = true;
        myIp = getExternalIp();


    }

    public void run() {

        serviceStarted();
    }

    public void close() throws IOException {

        isRuning = false;

        if (bcListener != null)
            bcListener.closeBC();

        if (socket != null)
            socket.close();
        while (!socket.isClosed()) ;

        if (mouseDatagramChannel != null)
            mouseDatagramChannel.close();

        if (keysDatagramChannel != null)
            keysDatagramChannel.close();

        if (serverSocket != null)
            serverSocket.close();


        clients.clear();
    }


    public void serviceStarted() {
        //means the service was registered successfully and we can now start receiving clients.
        //Loop that runs server functions

        try {
            mouseDatagramChannel = DatagramChannel.open();
            mouseDatagramChannel.socket().bind(new InetSocketAddress(0));
            mouseChannel = new CFMouseDatagramChannel(mouseDatagramChannel);
            new Thread(mouseChannel).start();

            keysDatagramChannel = DatagramChannel.open();
            keysDatagramChannel.socket().bind(new InetSocketAddress(0));
            keysChannel = new CFKeysDatagramChannel(keysDatagramChannel, clients);
            new Thread(keysChannel).start();


            socket = new DatagramSocket(serverSocket.getLocalPort(), InetAddress.getLocalHost());

            bcListener = new BCListener(socket.getLocalPort(),
                    keysChannel.getChannel().socket().getLocalPort(),
                    mouseChannel.getChannel().socket().getLocalPort());

            new Thread(bcListener).start();


        } catch (IOException e1) {
            LOGGER.log(Level.SEVERE, e1.toString(), e1);
            //e1.printStackTrace();
        }

        mainFrame.setIpAndPort();


        while (isRuning) {
            try {


                //Wait for a client to connect
                LOGGER.info("waiting for client " + socket.getLocalPort());
                //socket = serverSocket.accept();
                byte[] recvBuf = new byte[1024];
                packet = new DatagramPacket(recvBuf, recvBuf.length);

                socket.receive(packet);

                receivedMsg = new String(packet.getData()).trim();
                LOGGER.info("The message: " + receivedMsg);

                String[] splitedMsg = receivedMsg.split(":");
                LOGGER.info(splitedMsg[0]);

                switch (splitedMsg[0]) {

                    case "ControlyClient":
                        CFClient temp = new CFClient(splitedMsg[1], packet.getAddress().getHostAddress());
                        new Thread(new DeviceConnectedFrame(temp.getName())).start();
                        clients.add(temp);
                        mainFrame.addClientToLabel(temp);
                        break;
                    case "MacroStart":
                        LOGGER.info(macroBusy ? "macro busy" : " macro free");
                        if (!macroBusy) {
                            macroBusy = true;
                            LOGGER.info("Received Macro Start Msg");
                            if (Integer.parseInt(splitedMsg[1]) == 0)
                                mr = new MacroRecorder(false, packet.getAddress().getHostAddress());
                            else
                                mr = new MacroRecorder(true, packet.getAddress().getHostAddress());
                            mr.start();
                        }
                        break;
                    case "MacroStop":
                        LOGGER.info("Received Macro Stop Msg");
                        mr.stopRecord();
                        String m = mr.buildMacro();
                        mr.finishMacro();
                        byte[] macroBuffer = m.getBytes();
                        DatagramPacket sendMacro = new DatagramPacket(macroBuffer, macroBuffer.length, packet.getAddress(), packet.getPort());
                        socket.send(sendMacro);
                        LOGGER.info(m);
                        macroBusy = false;
                        break;

                    default:
                        LOGGER.warning("Received Wrong Message");
                        break;

                }

                for (CFClient c : clients) {
                    LOGGER.info(c.toString());
                }


                // System.out.println("Accepted connection from: " + socket.getRemoteSocketAddress());
                // CFPopup.incoming(socket.getInetAddress().getHostName(), socket.getRemoteSocketAddress().toString() ,tweenManager);


                //Create a new custom thread to handle the connection
                // CFClient client = new CFClient(socket, mouseChannel, keysChannel, messagesReceiver.getPort());

                //  clients.add(client);


                //Start the thread!
                // new Thread(client).start();


            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }

        }
    }


    // public int getPort() {
    //     return serverSocket.getLocalPort();
    // }

    public String getIP() {
        String ipNum = "";
        try {
            ipNum = (InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        return ipNum;
    }


    public String getExternalIp() {
        URL whatismyip = null;
        String ip = "";
        try {
            whatismyip = new URL("http://checkip.amazonaws.com");

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));

            ip = in.readLine(); //you get the IP as a String

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ip;
    }

    public String getPort() {
        return bcListener.getBC_PORT();
    }

    public String getMyIp() {
        return myIp;
    }


}
