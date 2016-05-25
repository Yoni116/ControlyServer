import java.awt.*;
import java.net.SocketAddress;
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
    String macroID;
    CFClient client;

    public MacroExecute(String macro, int mode, String macroID, CFClient client) {

        this.macroCommand = macro;
        this.mode = mode;
        this.macroID = macroID;
        this.client = client;
        executor = Executors.newFixedThreadPool(8);
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
        LOGGER.info("Running Macro : "+macroCommand);
        String[] keys = macroCommand.split(";");
        if (mode == 0) {
            robot.setAutoDelay(27);
            for (int i = 0; i < keys.length; i++) {
                String[] currentKey = keys[i].split(",");
                int key = Integer.parseInt(currentKey[0]);
                if(ControlyUtility.OSName.contains("Windows") && key  == 157)
                    key = 17;
                if (Integer.parseInt(currentKey[1]) == 0) {
                    LOGGER.info("pressing key: " + currentKey[0]);
                    robot.keyPress(key);
                } else {
                    LOGGER.info("releasing key: " + currentKey[0]);
                    robot.keyRelease(key);
                }
            }
        } else {
            LOGGER.info("Starting Timed Macro");
            robot.setAutoDelay(1);
            String[] currentKey = null;
            String[] nextKey = null;
            long nowPress = 0;
            long nextPress = 0;
            for (int i = 0; i < keys.length; i++) {

                try {
                    if(i==0) {
                        currentKey = keys[i].split(",");
                        nowPress = Integer.parseInt(currentKey[0]);
                        //this.sleep(nowPress);
                    }
                    executor.submit(new MacroKeyPress(Integer.parseInt(currentKey[1]),robot,Integer.parseInt(currentKey[2])));
                    if(i!=keys.length-1){
                        nextKey = keys[i+1].split(",");
                        nextPress = Integer.parseInt(nextKey[0]);
                        sleep(nextPress - nowPress);
                        nowPress = nextPress;
                        currentKey = nextKey;
                    }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }

            LOGGER.info("Finished Timed Macro");
        }
        try {
            if (client != null)
                client.sentFinishedMacro(macroID);
        } catch (Exception e){
            LOGGER.warning("problem sending macro finish msg to client");
            LOGGER.warning(e.getMessage());
        }


    }
}

