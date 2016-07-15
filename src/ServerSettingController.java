import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


public class ServerSettingController {


    @FXML
    private ImageView arrowBtn;



    @FXML
    private ImageView minimizeBtn;

    @FXML
    private CheckBox passCheckBox;

    @FXML
    private TextField passwordField;

    private Stage stage;
    private MainFrameFX mfFX;
    private CFService service;



    public ServerSettingController() {

    }

    @FXML
    void initialize() {
        assert arrowBtn != null : "fx:id=\"arrowBtn\" was not injected: check your FXML file 'ControlySettingFXML.fxml'.";

        assert minimizeBtn != null : "fx:id=\"minimizeBtn\" was not injected: check your FXML file 'ControlySettingFXML.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'ControlySettingFXML.fxml'.";

        passwordField.disableProperty().bind(passCheckBox.selectedProperty().not());



    }

    public void setMfFX(MainFrameFX mfFX) {
        this.mfFX = mfFX;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setService(CFService service) {
        this.service = service;
        passwordField.textProperty().bindBidirectional(service.passwordProperty());
        passCheckBox.selectedProperty().bindBidirectional(service.hasPasswordProperty());



    }

    @FXML
    public void minimizeToTray(){
        mfFX.hide(stage);
    }

    @FXML
    public void changeToInfo(){
        mfFX.changeScene(stage);
    }

    @FXML
    void enablePassword(ActionEvent event) {

//        if(passCheckBox.isSelected()){
//            passwordField.setEditable(true);
//        }
//        else {
//            passwordField.setEditable(false);
//        }


    }

}
