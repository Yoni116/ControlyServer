
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class ServerInfoController {


    @FXML
    private Label ipLabel;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private ImageView minimizeBtn;

    @FXML
    private Label portLabel;

    @FXML
    private ImageView settingBtn;

    private Stage stage;
    private MainFrameFX mfFX;


    public ServerInfoController() {

    }


    @FXML
    void initialize() {
        assert ipLabel != null : "fx:id=\"ipLabel\" was not injected: check your FXML file 'ControlyInfoFXML.fxml'.";
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'ControlyInfoFXML.fxml'.";
        assert minimizeBtn != null : "fx:id=\"minimizeBtn\" was not injected: check your FXML file 'ControlyInfoFXML.fxml'.";
        assert portLabel != null : "fx:id=\"portLabel\" was not injected: check your FXML file 'ControlyInfoFXML.fxml'.";
        assert settingBtn != null : "fx:id=\"settingBtn\" was not injected: check your FXML file 'ControlyInfoFXML.fxml'.";



    }



    public void setMfFX(MainFrameFX mfFX) {
        this.mfFX = mfFX;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void minimizeToTray(){
        mfFX.hide(stage);
    }

    @FXML
    public void changeToSetting(){
        mfFX.changeScene(stage);
    }

    public void setIpAndPort(String ip, String port){

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ipLabel.setText("IP: " + ip);
                portLabel.setText("PORT: " + port);

            }
        });

    }
}
