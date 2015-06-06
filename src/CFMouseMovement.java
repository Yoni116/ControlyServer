import java.awt.*;
import java.awt.event.InputEvent;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;


public class CFMouseMovement implements Runnable {

	private Robot mouse;
	private int x;
	private int y;
	private CharBuffer cBuff;
	private ByteBuffer buf;
	private String result;
	public CFMouseMovement(String point)
	{
		result = point;
		//buf = buffer;
		
	}
	
	public void run() 
	{
	 try {
		mouse = new Robot();
	} catch (AWTException e) {

		e.printStackTrace();
	}
	 
//	 NativeMouse nativeMouse = new NativeMouse();
	   Point mousePoint = MouseInfo.getPointerInfo().getLocation();
       x = (int)mousePoint.getX();
      y = (int)mousePoint.getY();
       
      
        String[] pointArray;
        
	 

      
       
     
     //System.out.println(result);
     
     if(result.equals("click"))
    		 {
    	 mouse.mousePress(InputEvent.BUTTON1_MASK);
         mouse.mouseRelease(InputEvent.BUTTON1_MASK);
    		 }
     else{
       pointArray = result.split(",", 2);
 	
      // System.out.println(result);







if((pointArray[0] == null) || (pointArray[1] == null))
return;



long recievedY = (int)Double.parseDouble(pointArray[1]);
long recievedX = (int)Double.parseDouble(pointArray[0]);

//calculations

//calculate the distance and determine speed
double distance = Math.sqrt((recievedX * recievedX) + (recievedY * recievedY));
if(distance <= 200)
	distance = 1;

if(distance > 200 && distance <= 500)
	distance = 3;

if(distance > 500 && distance <= 1000)
	distance = 30;

if(distance > 1000)
	distance = 70;


//System.out.println("distance is: " + distance);

//calculate the degrees
double radians = Math.atan2(recievedY, recievedX);
double degree = Math.toDegrees(radians);

//System.out.println("degrees are : " + degree);


//calculate new X and Y destination
double Y = Math.sin(radians) * distance;
double X = Math.cos(radians) * distance;

//System.out.println("Y = " + Y + " X = " + X);
//if((Y % 1) >= 50)
	//recievedY = (int)Math.ceil(Y);

recievedX = Math.round(X);
recievedY = Math.round(Y);



System.out.println(recievedX);
System.out.println(recievedY);


mousePoint = MouseInfo.getPointerInfo().getLocation();
x = (int) mousePoint.getX();
y = (int) mousePoint.getY();
//System.out.println(mousePoint);

mouse.mouseMove((int)(x + recievedX) , (int)(y + recievedY));
	 
		//We need a try-catch because lots of errors can be thrown
     }
	}
	
}
