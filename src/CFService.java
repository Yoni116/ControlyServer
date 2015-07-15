import aurelienribon.tweenengine.TweenManager;

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

public class CFService extends Thread implements CFServiceRegisterListener {

    private HashSet<CFClient> clients;
    private ServerSocket serverSocket;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private DatagramChannel mouseDatagramChannel;
    private DatagramChannel keysDatagramChannel;
    private CFKeysDatagramChannel keysChannel;
    private CFMouseDatagramChannel mouseChannel;
    private TweenManager tweenManager;
    private CFMessagesReceiver messagesReceiver;
    private BCListener bcListener;
    private boolean isRuning;
    private String receivedMsg;

    public CFService(TweenManager manager) throws IOException {
        clients = new HashSet<>();
        tweenManager = manager;
        //should create new exception object to deal with specifiec errors.
        serverSocket = new ServerSocket(0);
        //0 means choose an available port.
        // messagesReceiver = new CFMessagesReceiver();
        isRuning = true;


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
            keysChannel = new CFKeysDatagramChannel(keysDatagramChannel);
            new Thread(keysChannel).start();


            socket = new DatagramSocket(serverSocket.getLocalPort(), InetAddress.getLocalHost());

            bcListener = new BCListener(socket.getLocalPort(),
                    keysChannel.getChannel().socket().getLocalPort(),
                    mouseChannel.getChannel().socket().getLocalPort());

            new Thread(bcListener).start();


        } catch (IOException e1) {
            // TODO Auto-generated catch block
            //e1.printStackTrace();
        }


        while (isRuning) {
            try {


                //Wait for a client to connect
                System.out.println(new Timestamp(System.currentTimeMillis()) + " waiting for client " + socket.getLocalPort());
                //socket = serverSocket.accept();
                byte[] recvBuf = new byte[1024];
                packet = new DatagramPacket(recvBuf, recvBuf.length);

                socket.receive(packet);

                receivedMsg = new String(packet.getData()).trim();
                System.out.println(new Timestamp(System.currentTimeMillis()) + " The message: " + receivedMsg);

                String[] splitedMsg = receivedMsg.split(":");

                switch (splitedMsg[0]) {

                    case "ControlyClient":
                        clients.add(new CFClient(splitedMsg[1], packet.getAddress().getHostAddress()));
                        break;

                    default:
                        System.out.println("Received Wrong Message");
                        break;

                }

                for (CFClient c : clients) {
                    System.out.println(c.toString());
                }


                // System.out.println("Accepted connection from: " + socket.getRemoteSocketAddress());
                // CFPopup.incoming(socket.getInetAddress().getHostName(), socket.getRemoteSocketAddress().toString() ,tweenManager);


                //Create a new custom thread to handle the connection
                // CFClient client = new CFClient(socket, mouseChannel, keysChannel, messagesReceiver.getPort());

                //  clients.add(client);


                //Start the thread!
                // new Thread(client).start();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public String getIP() {
        String ipNum = "";
        try {
            ipNum = (InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        return ipNum;
    }

    public void serviceFailed() {
        //means the service has failed to register.
    }
}
