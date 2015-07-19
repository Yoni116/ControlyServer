import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hent on 3/26/15.
 */
public class KeyPress implements Runnable {
    private Robot robot;
    public String commandString;
    public int command;
    public Timer timer;
    public ConcurrentHashMap container;
    public boolean keyPressed;


    public KeyPress(String hex, ConcurrentHashMap containerRef, Robot robot) {

        this.keyPressed = true;
        this.commandString = hex;
        this.command = Integer.parseInt(hex, 16);
        this.container = containerRef;
        this.robot = robot;

    }

    public void run() {
        robot.keyPress(command);
        // System.out.println("pressed : 0x" + this.commandString + " which is: " + this.command);
        resetTimer();
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robot.keyRelease(command);
        while (keyPressed) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.keyPress(command);
        }

        robot.keyRelease(command);

        container.remove(commandString);

        System.out.println("released: " + command);
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
