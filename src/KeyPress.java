import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hent on 3/26/15.
 */
public class KeyPress extends Thread{
    private Robot robot;
    public String commandString;
    public int command;
    public Timer timer;
    public HashMap container;
    public boolean keyPressed;
    public CFKeysDatagramChannel myDatagram;



    public KeyPress(String hex,HashMap containerRef, CFKeysDatagramChannel dg) throws Exception{
        this.myDatagram = dg;
        this.keyPressed = true;
        this.commandString = hex;
        this.command = Integer.parseInt(hex,16);
        this.container = containerRef;
        this.robot = new Robot();
        robot.setAutoDelay(10);
    }

    public void run(){
        robot.keyPress(command);
       // System.out.println("pressed : 0x" + this.commandString + " which is: " + this.command);
        resetTimer();
        try {
            sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robot.keyRelease(command);
        while(keyPressed) {
            try {
                sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.keyPress(command);
        }

        robot.keyRelease(command);
        synchronized (myDatagram) {
            container.remove(commandString);
        }
        System.out.println("released: "+ command);
        }





    public synchronized void extendDeletion(){
        if(timer!=null){
            timer.cancel();
        }
        resetTimer();
    }

    public void reKey(){
      keyPressed=false;
    }

    public synchronized void resetTimer(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            reKey();
            }
        },20);


    }
}
