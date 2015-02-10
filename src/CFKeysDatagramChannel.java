import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;


public class CFKeysDatagramChannel implements Runnable{

	private Robot robot;
	private DatagramChannel channel;

	public CFKeysDatagramChannel(DatagramChannel c){
		channel = c;
	}
	
	public DatagramChannel getChannel() {
		return channel;
	}
	
    public void run()
    {
    	
        try {
           
         	System.out.println("Keys Ready");
			ByteBuffer buff = ByteBuffer.allocate(48);
	        Charset charSet = Charset.forName("UTF-8");  
	        CharsetDecoder coder = charSet.newDecoder();  
	        CharBuffer charBuff;
	              
			robot=new Robot();		
			
            while(true)
            {
   
//waiting for msg to arrive
             buff.clear();
             System.out.println("Keys waiting");
             channel.receive(buff);
             System.out.println("Keys got msg");
             buff.flip(); 
             	                              
             charBuff = coder.decode(buff);  
             String result = charBuff.toString().trim();
           	                               
             System.out.println("this is: " + result);
           	                    
   	         if(result != null){
   	         this.executeCommand(Integer.parseInt(result.substring(2), 16)); //makes the string an int and pass it to the execute method
   	      
     
   	                    	
   	           }
      	                    
                 buff.clear();

            }


        } catch( AWTException exception) {
            System.out.println("Error: " + exception);
        }   catch(IOException  exception) {
            System.out.println("Error: " + exception);
        }
    }
    
    
    //this method gets a hex virtual key code and execute it using ROBOT.
    private void executeCommand(int hex){
    	robot.keyPress(hex);
    }
    
    
}