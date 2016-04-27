
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Logger;

/**
 * Created by yoni on 08/07/2015.
 * Controly Server V6.0
 */
public class MainProgram  {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    //vars for file lock (one instance running)
    private static File f;
    private static FileChannel channel;
    private static FileLock lock;



    public static void main(String[] args) {

        //check if the program is already running
        try {
            f = new File("RingOnRequest.lock");
            // Check if the lock exist
            if (f.exists()) {
                // if exist try to delete it
                f.delete();
            }
            // Try to get the lock
            channel = new RandomAccessFile(f, "rw").getChannel();
            lock = channel.tryLock();
            if (lock == null) {
                // File is lock by other application
                channel.close();
                JOptionPane.showMessageDialog(null, "Controly Is Already Running");
                //throw new RuntimeException("Only 1 instance of Controly can run.");
                System.exit(0);
            }
            // Add shutdown hook to release lock when application shutdown
            ShutdownHook shutdownHook = new ShutdownHook();
            Runtime.getRuntime().addShutdownHook(shutdownHook);





        } catch (IOException e) {
            //throw new RuntimeException("Could not start process.", e);
            LOGGER.severe("Closing Controly - already running");
            System.exit(0);
        }




        try {
            ControlyLogger.setup();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.severe("Starting Controly V6.0");
        String javaArch = System.getProperty("os.arch");
        LOGGER.info("Java Architecture: " + javaArch);

        double version = Double.parseDouble(System.getProperty("java.specification.version"));
        LOGGER.info("Java JRE Version: " + version);

        //checking for Java version
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



                try {
                    //loading DLL for Windows OS
                    if (System.getProperty("os.name").contains("Windows")) {

                    if (javaArch.equals("amd64") || javaArch.equals("x86_64")) {
                        System.loadLibrary("keyListener");
                        LOGGER.info("Loading dll for 64bit system");
                    } else {
                        System.loadLibrary("keyListener32");
                        LOGGER.info("Loading dll for 32bit system");
                    }

                }
                    if(System.getProperty("os.name").contains("Mac")){
                        String path[] = MainProgram.class.getResource("MainProgram.class").toString().split("/");
                        String newPath = "/";
                        //change to length -5 for debugging mac and -3 for jar
                        for(int i = 1; i<= path.length - 5; i++){
                            newPath = newPath.concat(path[i]+"/");
                        }

                        System.load(newPath + "macListener.jnilib");


                       // LOGGER.info();
                    }

            } catch (UnsatisfiedLinkError e) {
                    LOGGER.severe(e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "keyListener DLL is missing please reinstall Controly");
                    LOGGER.severe("Closing Controly");
                    System.exit(0);

                }

            LOGGER.info("--------------------------------------------------------------");
            LOGGER.info(System.getenv("COMPUTERNAME"));
            LOGGER.info(ControlyUtility.OSName);

            LOGGER.info("Program Started");



            //starting the gui
            MainFrameFX.launch(MainFrameFX.class);

        }
    }

//file lock for single instance
    public static void unlockFile() {
        // release and delete file lock
        try {
            if (lock != null) {
                lock.release();
                channel.close();
                f.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ShutdownHook extends Thread {

        public void run() {
            unlockFile();
        }
    }




}