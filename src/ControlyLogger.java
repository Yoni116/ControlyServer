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
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String folder = "ControlyLogs/" + today;
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Timestamp(System.currentTimeMillis()));


        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


        logger.setLevel(Level.INFO);
        if(ControlyUtility.OSName.contains("Windows")) {
            System.out.println(new File("ControlyLogs").mkdir());
            System.out.println(new File(folder).mkdir());
            fileTxt = new FileHandler(folder + System.getProperty("file.separator") + timeStamp + "_Log.txt", 1000000, 2, true);
        }
        else{
            String path = System.getProperty("user.home")+System.getProperty("file.separator")+"ControlyLogs";
            new File(path).mkdir();
            new File(path+ System.getProperty("file.separator") + today).mkdir();
            fileTxt = new FileHandler(path + System.getProperty("file.separator")+ today + System.getProperty("file.separator")+ timeStamp + "_Log.txt", 1000000, 2, true);
        }




        // create a TXT formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);


    }
}


