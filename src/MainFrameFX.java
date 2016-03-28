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
    private MenuItem showApp;
    private MenuItem closeApp;

    private ServerInfoController sic;
    private ServerSettingController ssc;
    private CFService service;

    private NetworkInfo currentNetwork;

    private Scene infoScene;
    private Scene settingScene;

    private boolean isInfoScene;

    //JavaFX start method
    @Override
    public void start(Stage primaryStage) throws Exception{
        LOGGER.info("Frame Size: "+ frameSize);
        Platform.setImplicitExit(false);

        Dimension trayIconSize = tray.getTrayIconSize();

        trayIconeImg = new ImageIcon(MainFrameFX.class.getResource("/NewServerDesign/TrayIcon.png"))
                .getImage().getScaledInstance((int) trayIconSize.getWidth(), (int) trayIconSize.getHeight(), Image.SCALE_SMOOTH);

        createTrayIcon(primaryStage);




        FXMLLoader infoLoader = new FXMLLoader(getClass().getResource("ControlyInfoFXML.fxml"));
        FXMLLoader settingLoader = new FXMLLoader(getClass().getResource("ControlySettingFXML.fxml"));
        Parent infoRoot = infoLoader.load();
        Parent settingRoot = settingLoader.load();

        if(frameSize <= 550)
            frameSize = 550;
        infoScene = new Scene(infoRoot, frameSize, frameSize, javafx.scene.paint.Color.TRANSPARENT);
        settingScene = new Scene(settingRoot, frameSize, frameSize, javafx.scene.paint.Color.TRANSPARENT);
        primaryStage.setScene(infoScene);
        isInfoScene = true;
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Controly");

        ssc = settingLoader.getController();
        sic = infoLoader.getController();

        ssc.setStage(primaryStage);
        ssc.setMfFX(this);

        sic.setMfFX(this);
        sic.setStage(primaryStage);

        primaryStage.show();

        hide(primaryStage);


        startServer(sic);

        ssc.setService(service);
        sic.setIpAndPort(service.getMyIp(), service.getPort());

        // networkListener for network change
        currentNetwork = new NetworkInfo(ControlyUtility.getInetAddress(),service,this);
        currentNetwork.start();


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
        closeApp = new MenuItem("Exit");


        showApp.addActionListener(showListener);
        closeApp.addActionListener(closeListener);

        popup.add(showApp);
        popup.add(closeApp);

        //trayIcon.setPopupMenu(popup);


        // construct a TrayIcon
        trayIcon = new TrayIcon(trayIconeImg, "Controly", popup);
        // set the TrayIcon properties
        trayIcon.addActionListener(showListener);



    }


    public void hide(final Stage stage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                if (SystemTray.isSupported()) {
                    try {
                        tray = SystemTray.getSystemTray();
                        tray.add(trayIcon);
                        trayIcon.displayMessage("Server Is Running Minimized", "right click here if you want to close the server", TrayIcon.MessageType.INFO);
                    } catch (AWTException e) {
                        LOGGER.warning(e.getMessage());
                    }

                    stage.hide();

                } else {
                    System.exit(0);
                }
            }
        });
    }

    public void startServer(ServerInfoController sc) {
        LOGGER.info("Start");
        service = null;
        try {
            service = new CFService(sc);

        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        }
        service.start();


    }

    public void changeScene(Stage stage){

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (isInfoScene) {
                    stage.setScene(settingScene);
                    isInfoScene = false;
                } else {
                    stage.setScene(infoScene);
                    isInfoScene = true;
                }

            }
        });
    }

    public void resetService(){
        startServer(sic);
        currentNetwork.setService(service);
        LOGGER.warning("Finished resetting server");

    }
}
