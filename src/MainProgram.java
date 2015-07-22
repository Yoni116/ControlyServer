import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yoni on 08/07/2015.
 */
public class MainProgram {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {


        LOGGER.info(System.getenv("COMPUTERNAME"));
        LOGGER.info(System.getProperty("os.name"));

        LOGGER.info("Program Started");
        CFMainFrame frame = new CFMainFrame();
        frame.setFocusable(true);
        frame.setVisible(true);
        frame.requestFocus();
    }
}