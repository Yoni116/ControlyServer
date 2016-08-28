
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
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
import java.net.SocketException;
import java.util.logging.Logger;

/**
 * Created by yoni on 3/22/2016.
 */
public class MainFrameFX extends Application implements ActionListener {

    public static Stage mainStage;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    //vars for main window and tray
    static int frameSize = Math.min((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) / 2;

    private SystemTray tray = SystemTray.getSystemTray();
    private java.awt.Image trayIconeImg;
    private TrayIcon trayIcon;
    private PopupMenu popup;
    private MenuItem showApp;
    private MenuItem terms;
    private MenuItem privacy;
    private MenuItem closeApp;
    private NotificationPopup np;
    private PropertyFile pf;

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
        pf = new PropertyFile();
        mainStage = primaryStage;

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
        mainScene = new Scene(infoRoot, 420, 200, javafx.scene.paint.Color.TRANSPARENT);
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
        currentNetwork = new NetworkInfo(service, this);
        currentNetwork.start();

        ssc.setService(service);


        tray.add(trayIcon);

        np = new NotificationPopup("\nServer Is Running Minimized", "");
        np.start();



        final Delta dragDelta = new Delta();
        mainScene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = mainStage.getX() - mouseEvent.getScreenX();
                dragDelta.y = mainStage.getY() - mouseEvent.getScreenY();
            }
        });
        mainScene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                mainStage.setX(mouseEvent.getScreenX() + dragDelta.x);
                mainStage.setY(mouseEvent.getScreenY() + dragDelta.y);
            }
        });
        sic.setService(service);
    }

    //creating the tray icon and setting listeners
    public void createTrayIcon(final Stage stage) {

        if (!SystemTray.isSupported()) {
            LOGGER.warning("SystemTray is not supported");
            return;
        }

        popup = new PopupMenu();
        trayIcon = new TrayIcon(trayIconeImg);


        // create a popup menu
        showApp = new MenuItem("Show App");
        terms = new MenuItem("Terms & Conditions");
        privacy = new MenuItem("Privacy Policy");
        closeApp = new MenuItem("Exit");


        showApp.addActionListener(this);
        terms.addActionListener(this);
        privacy.addActionListener(this);
        closeApp.addActionListener(this);

        popup.add(showApp);
        popup.add(terms);
        popup.add(privacy)
        popup.add(closeApp);

        //trayIcon.setPopupMenu(popup);


        // construct a TrayIcon
        trayIcon = new TrayIcon(trayIconeImg, "Controly", popup);
        // set the TrayIcon properties
        trayIcon.addActionListener(this);

    }


    public void hide(final Stage stage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                if (SystemTray.isSupported()) {
                    try {

                        tray.add(trayIcon);
                        np = new NotificationPopup("\nServer Is Running Minimized", "");
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
        // networkListener for network change

        service = null;
        try {
            service = new CFService(sc,pf);

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

    public void resetService() {
        startServer(sic);
        currentNetwork.setService(service);
        sic.setIpAndPort(service.getMyIp(), service.getPort());
        LOGGER.warning("Finished resetting server");

    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {


        if (e.getSource().equals(showApp) || e.getSource().equals(trayIcon)) {
            if (trayIcon != null)
                tray.remove(trayIcon);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    mainStage.show();
                }
            });
        }

        if (e.getSource().equals(goToSite)) {
            BrowserOpener.openURL("http://www.controly.net");
        }

        if (e.getSource().equals(closeApp)) {
            System.exit(0);
        }

    }
}
class Delta { double x, y; }