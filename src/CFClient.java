import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class CFClient extends Thread{

	private Socket socket;
	private CFMouseDatagramChannel mouseDatagramChannel;
	private CFKeysDatagramChannel keysDatagramChannel;
	private int utilitySocketPort;
	boolean keepAlive;
	
    public CFClient(Socket s, CFMouseDatagramChannel mouseChannel, CFKeysDatagramChannel keysChannel, int utSocket){
    	socket = s;
    	mouseDatagramChannel = mouseChannel;
    	keysDatagramChannel = keysChannel;
    	keepAlive = true;
    	utilitySocketPort = utSocket;
    }

    
    //a
    //sends the datagram channels port and than waits for "ok" to be returned.
    // sends done when all ports where send.
	public void run() {
		
		  
		try {
			 PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			 BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		       
	         String chatInput = null;
	        
	       //Mouse Channel 
	   
	       output.print(mouseDatagramChannel.getChannel().socket().getLocalPort());
	       output.flush();
	       
	       chatInput = input.readLine();

           if(!chatInput.equals("ok"))
           {
           	System.out.println("Error");
           }else{
            System.out.println("Mouse channel port has been transferred");

           }
           
          
           //Keys Channel 
           chatInput = null;
	        
	       output.print(keysDatagramChannel.getChannel().socket().getLocalPort());
	       output.flush();
	       
	       chatInput = input.readLine();
	       

           if(!chatInput.equals("ok"))
           {
           	System.out.println("Error");
           }else{
               System.out.println("Keys channel port has been transferred");
           }
           
           
         //utilitySocket port
    	   
	       output.print(utilitySocketPort);
	       output.flush();
	       
	       chatInput = input.readLine();

           if(!chatInput.equals("ok"))
           {
           	System.out.println("Error");
           }else{
            System.out.println("Mouse channel port has been transferred");

           }
           
           
           
           
           System.out.print("done");
           output.print("done");
           output.flush();
           
           chatInput = null;
           
           
         
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
        
     
	
}
	
	public void kill(){
		keepAlive = false;
		this.notifyAll();
	}
	
	public void ping(){
		
	}

}
