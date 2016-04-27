import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by hent on 3/26/15.
 */
public class KeyPress implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Robot robot;
    private String commandString;
    private int command;
    private Timer timer;
    private ConcurrentHashMap container;
    private boolean keyPressed;
    private boolean cmdKey;


    public KeyPress(String hex, ConcurrentHashMap containerRef, Robot robot) {

        this.keyPressed = true;
        this.commandString = hex;
        this.command = Integer.parseInt(this.commandString, 16);
        this.container = containerRef;
        this.robot = robot;
        if(command ==( 16 | 17 | 18 | 157 ))
            cmdKey = true;

    }

    public void run() {
        robot.keyPress(command);

        resetTimer();
        try {
            Thread.sleep(27);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!cmdKey)
            robot.keyRelease(command);
        while (keyPressed) {
            try {
                Thread.sleep(27);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!cmdKey)
                robot.keyPress(command);
        }

        robot.keyRelease(command);

        container.remove(commandString);

        LOGGER.info("released: " + command);
    }


    public synchronized void extendDeletion() {
        if (timer != null) {
            timer.cancel();
        }
        resetTimer();
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
        }, 20);


    }
}
