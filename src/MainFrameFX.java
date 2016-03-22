import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by yoni on 3/22/2016.
 */
public class MainFrameFX extends Application {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    //vars for main window and tray
    static int frameSize = Math.min((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) / 2;

    private SystemTray tray = SystemTray.getSystemTray();
    private Image trayIconeImg;
    private TrayIcon trayIcon;
    private PopupMenu popup;
    private MenuItem aboutItem;
    private MenuItem showApp;
    private MenuItem closeApp;

    private ServerController sc;
    private CFService service;

    //JavaFX start method
    @Override
    public void start(Stage primaryStage) throws Exception{



        Platform.setImplicitExit(false);

        Dimension trayIconSize = tray.getTrayIconSize();

        trayIconeImg = new ImageIcon(MainFrame.class.getResource("/NewServerDesign/TrayIcon.png"))
                .getImage().getScaledInstance((int) trayIconSize.getWidth(), (int) trayIconSize.getHeight(), Image.SCALE_SMOOTH);

        createTrayIcon(primaryStage);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("ControlyFXML.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, frameSize, frameSize, javafx.scene.paint.Color.TRANSPARENT));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Controly");


        sc = (ServerController)loader.getController();

        sc.setMfFX(this);
        sc.setStage(primaryStage);

        primaryStage.show();

        hide(primaryStage);


        startServer();

    }

    //creating the tray icon and setting listeners
    public void createTrayIcon(final Stage stage) {

        if (!SystemTray.isSupported()) {
            LOGGER.warning("SystemTray is not supported");
            return;
        }

        popup = new PopupMenu();

        trayIcon = new TrayIcon(trayIconeImg);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {

                hide(stage);
            }
        });
        // create a action listener to listen for default action executed on the tray icon
        final ActionListener closeListener = new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.exit(0);
            }
        };

        ActionListener showListener = new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (trayIcon != null)
                            tray.remove(trayIcon);
                        stage.show();
                    }
                });
            }
        };
        // create a popup menu
        showApp = new MenuItem("Show App");
        aboutItem = new MenuItem("About");
        closeApp = new MenuItem("Exit");


//        popup.add(aboutItem);
//        popup.addSeparator();

        showApp.addActionListener(showListener);
        closeApp.addActionListener(closeListener);

        popup.add(showApp);
        popup.add(closeApp);

        //trayIcon.setPopupMenu(popup);


        // construct a TrayIcon
        trayIcon = new TrayIcon(trayIconeImg, "Controly", popup);
        // set the TrayIcon properties
        trayIcon.addActionListener(showListener);

//        try {
//            tray.add(trayIcon);
//        } catch (AWTException e) {
//            LOGGER.severe(e.getMessage());
//        }


    }


    public void hide(final Stage stage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                if (SystemTray.isSupported() && trayIcon != null) {
                    try {
                        tray.add(trayIcon);
                        trayIcon.displayMessage("Server Is Running Minimized", "right click here if you want to close the server", TrayIcon.MessageType.INFO);
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }

                    stage.hide();
                } else {
                    System.exit(0);
                }
            }
        });
    }

    public void startServer() {
        LOGGER.info("Start");

        try {
            service = new CFService(null);

        } catch (IOException e) {
            e.printStackTrace();
        }
        service.start();


    }
}
