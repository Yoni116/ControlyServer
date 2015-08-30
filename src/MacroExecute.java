import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by yoni on 30/08/2015.
 */
public class MacroExecute extends Thread {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private String macroCommand;
    private int mode;
    private ExecutorService executor;
    private Robot robot;

    public MacroExecute(String macro, int mode) {

        this.macroCommand = macro;
        this.mode = mode;
        executor = Executors.newFixedThreadPool(5);
        try {
            robot = new Robot();
            robot.setAutoWaitForIdle(true);
            robot.setAutoDelay(1);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        LOGGER.info(macroCommand);
        String[] keys = macroCommand.split(";");
        if (mode == 0) {
            robot.setAutoDelay(20);
            for (int i = 0; i < keys.length; i++) {
                String[] currentKey = keys[i].split(",");
                if (Integer.parseInt(currentKey[1]) == 0) {
                    LOGGER.info("pressing key: " + currentKey[0]);
                    robot.keyPress(Integer.parseInt(currentKey[0]));
                } else {
                    LOGGER.info("releasing key: " + currentKey[0]);
                    robot.keyRelease(Integer.parseInt(currentKey[0]));
                }
            }
        } else {


        }


    }
}

