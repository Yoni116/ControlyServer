import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by yoni on 30/08/2015.
 */
public class MacroKeyPress implements Runnable {
    private final int KEY_PRESS_TIME = 27;
    private final int FIRST_KEY_PRESS = 350;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Robot robot;
    private int command;
    private long duration;
    private Timer timer;
    private boolean keyPressed;
    private boolean cmdKey;


    public MacroKeyPress(int command, Robot robot, long duration) {

        this.keyPressed = true;
        this.duration = duration;
        this.command = command;
        this.robot = robot;
        if(command ==( 16 | 17 | 18 ))
            cmdKey = true;

    }


    public void run() {
        resetTimer();
        robot.keyPress(command);
        try {
            Thread.sleep(FIRST_KEY_PRESS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!cmdKey)
            robot.keyRelease(command);
        while (keyPressed) {
            try {
                Thread.sleep(KEY_PRESS_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!cmdKey)
                robot.keyPress(command);
        }
        robot.keyRelease(command);

        LOGGER.info("released: " + command);
    }




    public void reKey() {
        keyPressed = false;
    }

    public synchronized void resetTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                reKey();
            }
        }, this.duration);


    }


}
