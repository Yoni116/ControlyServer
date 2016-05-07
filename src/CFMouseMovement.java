import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.geom.Rectangle2D;
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

        String[] mouseCommand = result.split(";");
        switch (mouseCommand[0]) {
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
            case "mouseScroll":
                mouse.mouseWheel(Integer.parseInt(mouseCommand[1]));
            default:
                mouseMovement(mouseCommand[0]);
                break;
        }


    }


    private void mouseMovement(String result) {


        String[] pointArray;

        pointArray = result.split(",", 2);

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


//calculate the degrees
        double radians = Math.atan2(recievedY, recievedX);

//calculate new X and Y destination
        double Y = Math.sin(radians) * distance;
        double X = Math.cos(radians) * distance;

        recievedX = Math.round(X);
        recievedY = Math.round(Y);


        LOGGER.info("Move Mouse by (" + recievedX + "," + recievedY + ")");


        Point mousePoint = MouseInfo.getPointerInfo().getLocation();
        x = mousePoint.getX();
        y = mousePoint.getY();

        Rectangle2D screenSize = new Rectangle2D.Double();
        GraphicsEnvironment localGE = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice gd : localGE.getScreenDevices()) {
            for (GraphicsConfiguration graphicsConfiguration : gd.getConfigurations()) {
                screenSize.union(screenSize, graphicsConfiguration.getBounds(), screenSize);
            }
        }

        int finalMoveX = (int) (x + recievedX);
        int finalMoveY = (int) (y + recievedY);

        // trying to block mouse from going over bounds (only happens on Mac osx)
//        if(finalMoveX < 0)
//            finalMoveX = 0;
//        if(finalMoveY < 0)
//            finalMoveY = 0;
//        if(finalMoveX > screenSize.getWidth())
//            finalMoveX = (int)screenSize.getWidth();
//        if(finalMoveY > screenSize.getHeight())
//            finalMoveY = (int)screenSize.getWidth();

        LOGGER.info("Move Mouse to (" + finalMoveX + "," + finalMoveY + ")");
        mouse.mouseMove(finalMoveX, finalMoveY);


    }

}
