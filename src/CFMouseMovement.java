import java.awt.*;
import java.awt.event.InputEvent;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.logging.Logger;


public class CFMouseMovement implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private double x;
    private double y;
    private Robot mouse;


    private String result;

    public CFMouseMovement(String point, Robot mouse) {
        result = point;
        this.mouse = mouse;

    }

    public void run() {


        //System.out.println(result);


        switch (result) {
            case "leftClick":
                mouse.mousePress(InputEvent.BUTTON1_MASK);
                mouse.mouseRelease(InputEvent.BUTTON1_MASK);
                break;
            case "rightClick":
                mouse.mousePress(InputEvent.BUTTON3_MASK);
                mouse.mouseRelease(InputEvent.BUTTON3_MASK);
                break;
            case "middleClick":
                mouse.mousePress(InputEvent.BUTTON2_MASK);
                mouse.mouseRelease(InputEvent.BUTTON2_MASK);
                break;
            default:
                mouseMovement(result);
                break;
        }

    }


    private void mouseMovement(String result) {

        Point mousePoint = MouseInfo.getPointerInfo().getLocation();
        x = mousePoint.getX();
        y = mousePoint.getY();


        String[] pointArray;

        pointArray = result.split(",", 2);

        // System.out.println(result);


        if ((pointArray[0] == null) || (pointArray[1] == null))
            return;


        double recievedY = Double.parseDouble(pointArray[1]);
        double recievedX = Double.parseDouble(pointArray[0]);

//calculations

//calculate the distance and determine speed
        double distance = Math.sqrt((recievedX * recievedX) + (recievedY * recievedY));
        if (distance <= 200)
            distance = 1;

        if (distance > 200 && distance <= 500)
            distance = 3;

        if (distance > 500 && distance <= 1000)
            distance = 30;

        if (distance > 1000)
            distance = 70;


//System.out.println("distance is: " + distance);

//calculate the degrees
        double radians = Math.atan2(recievedY, recievedX);


//System.out.println("degrees are : " + degree);


//calculate new X and Y destination
        double Y = Math.sin(radians) * distance;
        double X = Math.cos(radians) * distance;

//System.out.println("Y = " + Y + " X = " + X);


        recievedX = Math.round(X);
        recievedY = Math.round(Y);


        LOGGER.info("Move Mouse by (" + recievedX + "," + recievedY + ")");


        mousePoint = MouseInfo.getPointerInfo().getLocation();
        x = mousePoint.getX();
        y = mousePoint.getY();
//System.out.println(mousePoint);

        mouse.mouseMove((int) (x + recievedX), (int) (y + recievedY));

        //We need a try-catch because lots of errors can be thrown
    }

}
