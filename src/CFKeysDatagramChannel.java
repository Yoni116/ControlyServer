import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;


public class CFKeysDatagramChannel implements Runnable{

	private Robot robot;
	private DatagramChannel channel;
    private HashMap<String,KeyPress> pressedKeys = new HashMap<String, KeyPress>();

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
	              
			//robot=new Robot();
			
            while(true)
            {
   
//waiting for msg to arrive
             buff.clear();
           //  System.out.println("Keys waiting");
             channel.receive(buff);
           //  System.out.println("Keys got msg");
             buff.flip(); 
             	                              
             charBuff = coder.decode(buff);  
             String result = charBuff.toString().trim();
           	                               
             System.out.println("this is: " + result);

                if(result!=null){
                    final String command = result.substring(2);
                    if(pressedKeys.containsKey(command)){
                        System.out.println("extends "+ command);
                        pressedKeys.get(command).extendDeletion();
                    }else{
                        KeyPress key = new KeyPress(command,pressedKeys, this);
                        key.start();
                        pressedKeys.put(command,key);
                    }


                }else{
                    System.out.println("Received a null key");
                }




/*

   	         if(result != null){
                 final String command = result.substring(2, 16);
                 if(pressedKeys.containsKey(command)){
                 if(System.nanoTime() - pressedKeys.get(command) > 80000000){
                     robot.keyRelease(Integer.parseInt(command));
                     pressedKeys.remove(command);
                 }else {

                 }

                 }else{/*
                     Thread t1 = new Thread(new Runnable() {
                         public void run() {
                             executeCommand(Integer.parseInt(command)); //makes the string an int and pass it to the execute method

                             System.out.println("pressed " + command);
                             pressedKeys.put(command,System.nanoTime());
                         }
                     });
                     t1.start();
                     */



                // }
   	           //}
      	                    
                 buff.clear();

            }


        } catch( Exception e) {
            e.printStackTrace();
        }
    }
    
    
    //this method gets a hex virtual key code and execute it using ROBOT.
    private void executeCommand(int hex){
    	robot.keyPress(hex);
        robot.delay(100);
        robot.keyRelease(hex);
    }
    

}