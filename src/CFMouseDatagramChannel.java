import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;



public class CFMouseDatagramChannel implements Runnable{

	private DatagramChannel channel;
	private Robot robot;
	
	public CFMouseDatagramChannel(DatagramChannel c){
		channel = c;
	}
	
public DatagramChannel getChannel() {
	return channel;
}
	
	public void run() 
	{ 
		
		System.out.println("MousePad Opened");
		ByteBuffer buf = ByteBuffer.allocate(48);
	
                Charset charSmth = Charset.forName("UTF-8");  
                CharsetDecoder coder = charSmth.newDecoder();  
                CharBuffer cBuff;
                String[] pointArray;
                
		//We need a try-catch because lots of errors can be thrown
        try {
        	
         //get mouse starting position
         Point mousePoint = MouseInfo.getPointerInfo().getLocation();
         int x = (int)mousePoint.getX();
         int y = (int)mousePoint.getY();
         System.out.println("mouse pos is x: " + x + " y: " + y);
        	 
		 robot = new Robot();
			
          while(true)
          {
            //someone is sending us data
        	  buf.clear();
	        	//  System.out.println("Keys waiting");
	                channel.receive(buf);
	               // System.out.println("mouse got smth");
	               buf.flip(); 

	                 cBuff = coder.decode(buf);  
	                 String result = cBuff.toString().trim();
	             
   	              CFMouseMovement cFMouseMovement = new CFMouseMovement(result);
                 	new Thread (cFMouseMovement).start();  
	
                  buf.clear();
       
                              
            }                  
          
     
                               
            } catch( AWTException exception) {
                System.out.println("Error: " + exception);
            }   catch(IOException  exception) {
		System.out.println("Error: " + exception);
	}
        }

}
