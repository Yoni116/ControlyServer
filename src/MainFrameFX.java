
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.scene.image.Image;

import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.TrayNotification;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by yoni on 3/22/2016.
 */
public class MainFrameFX extends Application {

    public static Stage mainStage;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    //vars for main window and tray
    static int frameSize = Math.min((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) / 2;

    private SystemTray tray = SystemTray.getSystemTray();
    private java.awt.Image trayIconeImg;
    private TrayIcon trayIcon;
    private PopupMenu popup;
    private MenuItem showApp;
    private MenuItem closeApp;
    private NotificationPopup np;

    private ServerInfoController sic;
    private ServerSettingController ssc;
    private CFService service;

    private NetworkInfo currentNetwork;

    private Scene mainScene;


    private boolean isInfoScene;

    private Parent infoRoot;
    private Parent settingRoot;


    //JavaFX start method
    @Override
    public void start(Stage primaryStage) throws Exception{

        mainStage = primaryStage;
//        Stage pStage = new Stage();
//        pStage.initStyle(StageStyle.UTILITY);
//        pStage.show();
//        np = new NotificationPopup("a",3);
//        np.show(pStage,0,0);


        LOGGER.info("Frame Size: "+ frameSize);
        Platform.setImplicitExit(false);

        Dimension trayIconSize = tray.getTrayIconSize();

        trayIconeImg = new ImageIcon(MainFrameFX.class.getResource("/NewServerDesign/TrayIcon.png"))
                .getImage().getScaledInstance((int) trayIconSize.getWidth(), (int) trayIconSize.getHeight(), java.awt.Image.SCALE_SMOOTH);

        createTrayIcon(primaryStage);

        FXMLLoader infoLoader = new FXMLLoader(getClass().getResource("ControlyInfoFXML.fxml"));
        FXMLLoader settingLoader = new FXMLLoader(getClass().getResource("ControlySettingFXML.fxml"));
        infoRoot = infoLoader.load();
        settingRoot = settingLoader.load();

        if(frameSize <= 550)
            frameSize = 550;
        mainScene = new Scene(infoRoot, frameSize, frameSize, javafx.scene.paint.Color.TRANSPARENT);
        primaryStage.setScene(mainScene);
        isInfoScene = true;
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Controly");

        primaryStage.getIcons().add(new Image("/NewServerDesign/TrayIcon.png"));

        ssc = settingLoader.getController();
        sic = infoLoader.getController();

        ssc.setStage(primaryStage);
        ssc.setMfFX(this);

        sic.setMfFX(this);
        sic.setStage(primaryStage);

        startServer(sic);

        ssc.setService(service);
        sic.setIpAndPort(service.getMyIp(), service.getPort());

        // networkListener for network change
        currentNetwork = new NetworkInfo(ControlyUtility.getInetAddress(),service,this);
        currentNetwork.start();
        tray.add(trayIcon);

        np = new NotificationPopup("Server Is Running Minimized","");
        np.start();

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
                t.consume();
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

                        tray.add(trayIcon);
                        //trayIcon.displayMessage("Server Is Running Minimized", "right click here if you want to close the server", TrayIcon.MessageType.INFO);
                        np = new NotificationPopup("Server Is Running Minimized","");
                        np.start();
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
                    mainScene.setRoot(settingRoot);
                    isInfoScene = false;
                } else {
                    mainScene.setRoot(infoRoot);
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
