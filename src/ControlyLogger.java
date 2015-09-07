import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;
import java.util.logging.SimpleFormatter;

/**
 * Created by yoni on 22/07/2015.
 */
public class ControlyLogger {

    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;


    static public void setup() throws IOException {

        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


        logger.setLevel(Level.INFO);
        new File("Logs").mkdir();
        fileTxt = new FileHandler("Logs/" + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "Log.txt", 500000, 1, true);


        // create a TXT formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);


    }
}


