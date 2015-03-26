import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hent on 3/26/15.
 */
public class KeyPress implements  Runnable{
    private Robot robot;
    public String commandString;
    public int command;
    public Timer timer;
    public HashMap container;


    public KeyPress(String hex,HashMap containerRef) throws Exception{
    this.commandString = hex;
    this.command = Integer.parseInt(hex,16);
    this.container = containerRef;
    this.robot = new Robot();
        run();
    }

    public void run(){
        robot.keyPress(command);
        System.out.println("pressed : " + this.commandString + " which is: " + this.command);
        resetTimer();
    }



    public void extendDeletion(){
        if(timer!=null){
            timer.cancel();
        }
        resetTimer();
    }

    public void resetTimer(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                robot.keyRelease(command);
                container.remove(commandString);
                System.out.println("released: "+ command);

            }
        },100);


    }
}
