import java.io.IOException;
import java.net.*;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * the BCListener class is a support service class for the main server
 * its main job is to listen to broadcasts from clients that wants to connect to the server
 * and sends them a replay with the server info
 *
 * @author Yoni Maymon
 * @version 1.0
 * @since 02/07/2015
 * P.S. : to @hen this is how you write code comments !!!!
 */

public class BCListener extends Thread {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final int BC_PORT = 56378;
    private final String MC_ADDR = "224.0.1.217";
    private InetAddress group;

    //private int threadNum = ControlyUtility.number++;

    private InetAddress localAddress;

    private DatagramSocket socket;
    private MulticastSocket mcSocket;
    private int connectionPort, keysPort, mousePort;
    private boolean serverRunning;

    private CFService server;



    /**
     * main constructor for class
     * it receives the connection port from the main server
     *
     * @param mainPort the port to sent to the client for connection
     */
    public BCListener(int mainPort, int kPort, int mPort, CFService server) {
        this.connectionPort = mainPort;
        this.mousePort = mPort;
        this.keysPort = kPort;
        this.serverRunning = true;
        this.localAddress = ControlyUtility.localAddress;
        this.server = server;
    }

    @Override
    public void run() {
        try {

            // port 56378 will always be used for bc reason
            group = InetAddress.getByName(MC_ADDR);
            mcSocket = new MulticastSocket(BC_PORT);
            mcSocket.setNetworkInterface(NetworkInterface.getByInetAddress(localAddress));
            mcSocket.joinGroup(group);
            socket = new DatagramSocket(0);
            socket.setBroadcast(true);


            while (serverRunning) {
                LOGGER.info("Ready to receive broadcast packets on port: " + BC_PORT + " !");

                String reply = "controly:" +
                        InetAddress.getLocalHost().getHostName() +
                        ":" + connectionPort +
                        ":" + keysPort +
                        ":" + mousePort +
                        ":" + localAddress.getHostAddress();
                LOGGER.info(reply);
                //Receive a packet
                byte[] recvBuf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                mcSocket.receive(packet);

                //Packet received
                LOGGER.info("Discovery packet received from: " + packet.getAddress().getHostAddress());
                LOGGER.info("Packet received; data: " + new String(packet.getData()));

                //See if the packet holds the right command (message)

                String message = new String(packet.getData()).trim();
                if (message.equals("CONTROLY DISCOVER REQUEST")) {



                    if(server.getHasPassword()){
                        reply = reply.concat(":YES");
                    }
                    else
                        reply = reply.concat(":NO");

                    byte[] sendData = reply.getBytes();

                    //Send a response
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);

                    LOGGER.info("Sent packet to: " + sendPacket.getAddress().getHostAddress());


                }

                recvBuf = null;
            }

        } catch (SocketException e) {
            LOGGER.warning("BCListener closed ignore exception");
            LOGGER.log(Level.SEVERE, e.toString(), e);
        } catch (UnknownHostException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }

    }


    /**
     * let the service know that the server have been closed
     * so it cant stop listning and die
     */
    public void closeBC() {
        serverRunning = false;
        try {
            mcSocket.leaveGroup(InetAddress.getByName(MC_ADDR));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mcSocket.close();
        socket.close();

    }

    public String getBC_PORT() {
        return "" + this.BC_PORT;
    }


}
