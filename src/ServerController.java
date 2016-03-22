import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;

/**
 * Created by yoni on 3/22/2016.
 */
public class ServerController {

    @FXML
    private AnchorPane mainPane;

    private MainFrameFX mfFX;
    private Stage stage;

    public ServerController() {

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
}
