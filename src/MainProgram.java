
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Created by yoni on 08/07/2015.
 * Controly Server V3.2
 */
public class MainProgram {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {

        try {
            ControlyLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.severe("Starting Controly");
        String javaArch = System.getProperty("os.arch");
        LOGGER.info("Java Architecture: " + javaArch);

        double version = Double.parseDouble(System.getProperty("java.specification.version"));
        LOGGER.info("Java JRE Version: " + version);

        if (version < 1.8) {
            LOGGER.severe("JRE version not compatible");
            JLabel label = new JLabel();

            JEditorPane ep = new JEditorPane("text/html", "<html><body>" //
                    + "This program can only run with java 8 or higher <br/>" +
                    "please update your java version at: <a href=\"https://java.com/en/download/\">https://java.com/en/download/</a>" //
                    + "</body></html>");

            // handle link events
            ep.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {

                    if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                            LOGGER.severe("Closing Controly");
                            System.exit(0);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                }
            });
            ep.setEditable(false);
            ep.setBackground(label.getBackground());

            // show
            JOptionPane.showMessageDialog(null, ep);
            LOGGER.severe("Closing Controly");
        } else {
            if (System.getProperty("os.name").contains("Windows")) {


                try {
                    if (javaArch.equals("amd64") || javaArch.equals("x86_64")) {
                        System.loadLibrary("keyListener");
                        LOGGER.info("Loading dll for 64bit system");
                    } else {
                        System.loadLibrary("keyListener32");
                        LOGGER.info("Loading dll for 32bit system");
                    }

                } catch (UnsatisfiedLinkError e) {
                    LOGGER.severe(e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "keyListener DLL is missing please reinstall Controly");
                    LOGGER.severe("Closing Controly");
                    System.exit(0);

                }
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
}