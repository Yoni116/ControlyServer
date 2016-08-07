import java.io.*;
import java.util.Properties;

/**
 * Created by yoni on 08/07/2016.
 */
public class PropertyFile {

    private Properties prop;
    private File yourFile;
    private InputStream input;
    private OutputStream output;
    private String filename = "Controly.config";

    private boolean hasPassword = false;
    private String password = "";
    private String serverID = "";

    public PropertyFile() {
        prop = new Properties();

        yourFile = new File(filename);
        if (!yourFile.exists()) {
            createNewFile();
        } else {
            loadFromFile();
        }


    }

    private void createNewFile() {

        serverID = "" + ((int) (Math.random() * 9000) + 1000);
        ControlyUtility.serverID = serverID;
        saveToFile();

    }

    private void loadFromFile() {
        try {
            input = new FileInputStream(yourFile);
            prop.load(input);

            ControlyUtility.serverID = prop.getProperty("ServerID");
            if (prop.getProperty("hasPassword").equals("YES"))
                hasPassword = true;
            else
                hasPassword = false;
            password = prop.getProperty("password");

            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToFile() {
        try {
            output = new FileOutputStream(yourFile, false);
            prop.setProperty("serverID", serverID);
            prop.setProperty("hasPassword", hasPassword ? "YES" : "NO");
            prop.setProperty("password", password);

            prop.store(output, null);


            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isHasPassword() {
        return hasPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
        saveToFile();

    }

    public void setPassword(String password) {
        this.password = password;
        saveToFile();
    }
}
