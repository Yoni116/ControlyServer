import aurelienribon.tweenengine.TweenManager;

import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CFService extends Thread implements CFServiceRegisterListener {

     private ArrayList<CFClient> clients;
     private RegisterService register;
     private ServerSocket serverSocket;
     private DatagramChannel mouseDatagramChannel;
     private DatagramChannel keysDatagramChannel;
     private CFKeysDatagramChannel keysChannel;
     private CFMouseDatagramChannel mouseChannel;
     private TweenManager tweenManager;
     private CFMessagesReceiver messagesReceiver;
	private BCListener bcListener;
	
	public CFService(TweenManager manager) throws IOException{
		clients = new ArrayList<CFClient>();
		tweenManager = manager;
		//should create new exception object to deal with specifiec errors.
		
		serverSocket = new ServerSocket(0); //0 means choose an available port.
		messagesReceiver = new CFMessagesReceiver();
		
	}

	public void run() {
		bcListener = new BCListener(serverSocket.getLocalPort());
		new Thread(bcListener).start();
		serviceStarted();
	}

	public void close() {
		if(register != null){
			keysChannel.endThreadPool();
			register.stopService();
			register = null;
		}

		if (bcListener != null)
			bcListener.closeBC();

		clients.clear();
	}
	
    
    public void serviceStarted(){
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


		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       
    
        while(true) {
        	try {
        		 //Wait for a client to connect
            	System.out.println("waiting for client");
                Socket socket = serverSocket.accept();
                System.out.println("Accepted connection from: " + socket.getRemoteSocketAddress());
               // CFPopup.incoming(socket.getInetAddress().getHostName(), socket.getRemoteSocketAddress().toString() ,tweenManager);
            
              
              
                 
                //Create a new custom thread to handle the connection
                CFClient client = new CFClient(socket,mouseChannel,keysChannel,messagesReceiver.getPort());
               
                clients.add(client);
                
             
                //Start the thread!
                new Thread (client).start();
               
                 
			} catch (Exception e) {
				e.printStackTrace();

				
			}
           
        }
    }
    
    public void checkClients(){
    	Timer timer = new Timer();
    	timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				for(int i=0; i < clients.size() ; i++){
		    		clients.notifyAll();
		    	}	
			}
		}, 60000);
    	
    		
    }
    public int getPort(){
		return serverSocket.getLocalPort();
	}
	public String getIP(){
		String ipNum = "";
		try {
			ipNum =(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		return ipNum;
	}
    public void serviceFailed(){
    //means the service has failed to register.	
    }
}
