import java.awt.*;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by yoni on 30/08/2015.
 */
public class MacroKeyPress {
    private final int KEY_PRESS_TIME = 27;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Robot robot;
    private String commandString;
    private int command;
    private long duration;
    private int mode;
    //private Timer timer;
    //private ConcurrentHashMap container;
    private boolean keyPressed;


    public MacroKeyPress(String hex, Robot robot, int mode) {

        this.keyPressed = true;
        this.commandString = hex;
        this.command = Integer.parseInt(hex, 16);
        this.mode = mode;
        this.robot = robot;

    }


}
