import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.TrayNotification;

import java.awt.*;
import java.io.IOException;

/**
 * Created by yoni on 3/28/2016.
 */
public class NotificationPopup extends Thread {

    private TrayNotification tn;
    private int type;
    private String title;
    private String msg;


    public NotificationPopup(String title, String msg) {
        this.title = title;
        this.msg = msg;

    }


    public void run() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tn = new TrayNotification();
                tn.setRectangleFill(Paint.valueOf("#43b7e3"));
                tn.setAnimationType(AnimationType.FADE);
                tn.setImage(new Image("/NewServerDesign/TrayIcon.png"));
                tn.setTitle(title);
                tn.setMessage(msg);
                tn.showAndDismiss(Duration.seconds(1));
            }
        });


    }
}






