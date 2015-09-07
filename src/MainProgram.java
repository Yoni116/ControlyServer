import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by yoni on 08/07/2015.
 */
public class MainProgram {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {

        LOGGER.info("starting");
        String javaArch = System.getProperty("os.arch");
        LOGGER.info("Java Architecture: " + javaArch);

        try {
            ControlyLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (javaArch.equals("amd64") || javaArch.equals("x86_64")) {
                System.loadLibrary("keyListner");
                LOGGER.info("Loading dll for 64bit system");
            } else {
                System.loadLibrary("keyListner32");
                LOGGER.info("Loading dll for 32bit system");
            }

        } catch (UnsatisfiedLinkError e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();

        }

        LOGGER.info("--------------------------------------------------------------");
        LOGGER.info(System.getenv("COMPUTERNAME"));
        LOGGER.info(System.getProperty("os.name"));

        LOGGER.info("Program Started");
        MainFrame frame = new MainFrame();

        frame.setFocusable(true);
        frame.requestFocus();
    }
}