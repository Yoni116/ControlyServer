

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.collections.*;
import javafx.scene.control.Label;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CFService extends Thread {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final ObservableList<Label> listItems = FXCollections.observableArrayList();

    private HashSet<CFClient> clients;
    private List<Label> clientNames;
    private InetAddress localAddress;
    private ServerSocket serverSocket;
    private Socket socket;
    private DatagramPacket receivedPacket;
    private DatagramChannel mouseDatagramChannel;
    private DatagramChannel keysDatagramChannel;
    private CFKeysDatagramChannel keysChannel;
    private CFMouseDatagramChannel mouseChannel;
    private CFMessagesReceiver messagesReceiver;
    private BCListener bcListener;
    private boolean isRuning;
    private String receivedMsg;
    private ServerInfoController sc;
    private String myIp;

    private SimpleBooleanProperty hasPassword;
    private SimpleStringProperty password;

    // private MacroRecorder mr;
    private boolean macroBusy;

//    private String returnMsg;
//    private byte[] msgBuffer;
//    private byte[] recvBuf;
//    private DatagramPacket returnPacket;

    public CFService(ServerInfoController sc) throws IOException {
        password = new SimpleStringProperty("");
        hasPassword = new SimpleBooleanProperty(false);
        this.sc = sc;
        macroBusy = false;
        clients = new HashSet<>();
        clientNames = new ArrayList<>();

        //should create new exception object to deal with specifiec errors.
        serverSocket = new ServerSocket(0);
        //0 means choose an available port.
        // messagesReceiver = new CFMessagesReceiver();
        isRuning = true;
        //myIp = getExternalIp();
        localAddress = ControlyUtility.getInetAddress();


    }

    public void run() {

        serviceStarted();
    }

    public void close() throws IOException {

        isRuning = false;

        if (bcListener != null)
            bcListener.closeBC();


        if (serverSocket != null)
            serverSocket.close();
        while (!serverSocket.isClosed()) ;

        if (mouseDatagramChannel != null)
            mouseDatagramChannel.close();

        if (keysDatagramChannel != null)
            keysDatagramChannel.close();




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


            //socket = new DatagramSocket(serverSocket.getLocalPort(), localAddress);

            bcListener = new BCListener(serverSocket.getLocalPort(),
                    keysChannel.getChannel().socket().getLocalPort(),
                    mouseChannel.getChannel().socket().getLocalPort(),this);

            new Thread(bcListener).start();





        } catch (IOException e1) {
            LOGGER.log(Level.SEVERE, e1.toString(), e1);
            //e1.printStackTrace();
        }

//        mainFrame.setIpAndPort();
        sc.setIpAndPort(getMyIp(),getPort());

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                pingAllClients();

            }
        }, 0, 20000);


        while (isRuning) {

            try {

                //Wait for a client to connect
                LOGGER.info("waiting for client " + serverSocket.getLocalPort());
                socket = serverSocket.accept();
                LOGGER.info("Server received connection from: " + socket.getInetAddress().toString());
                // TODO change first null to Server Controller
                CFClient temp = new CFClient(socket, socket.getInetAddress().toString(), this, keysChannel.getChannel().socket().getLocalPort(), mouseChannel.getChannel().socket().getLocalPort());
                clients.add(temp);
                temp.start();


            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }

        }
    }


    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public boolean getHasPassword() {
        return hasPassword.get();
    }

    public SimpleBooleanProperty hasPasswordProperty() {
        return hasPassword;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword.set(hasPassword);
    }

    public void addClientName(String name){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Label temp = new Label(name);
                listItems.add(temp);
            }
        });


    }

    public void removeClientName(String name){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                for (Label l: listItems){
                    if(l.getText().equals(name)) {
                        listItems.remove(l);
                        break;
                    }
                }
            }
        });
    }

    public ObservableList<Label> getListItems() {
        return listItems;
    }

    public String getExternalIp() {
        URL whatismyip;
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
        //return bcListener.getBC_PORT();
        return Integer.toString(serverSocket.getLocalPort());
    }

    public String getMyIp() {

        //return myIp;
        return localAddress.getHostAddress();
    }

    public synchronized void removeClient(CFClient cl) {
        System.out.println(clients.remove(cl));
    }

    public synchronized void printClients() {
        for (CFClient c : clients) {
            LOGGER.info(c.toString());
        }

    }

    public synchronized void pingAllClients() {
        LOGGER.info("Connected Clients List:");
        boolean haveActiveClients = false;
        for (CFClient c : clients) {
            LOGGER.info(c.toString());
            if (!c.isSuspended())
                haveActiveClients = true;
        }
        if (!clients.isEmpty() || haveActiveClients) {
            // clients.forEach(CFClient::toString);
            LOGGER.info("Starting to Ping all connected clients");
            clients.forEach(CFClient::pingClient);
        }
    }

    public void resetBCListner() {
        bcListener.closeBC();
        bcListener = new BCListener(serverSocket.getLocalPort(),
                keysChannel.getChannel().socket().getLocalPort(),
                mouseChannel.getChannel().socket().getLocalPort(),this);
        new Thread(bcListener).start();
        sc.setIpAndPort(getMyIp(), getPort());

    }


}
