package com.nargiz.chess.client.ui.dialogs;

import com.nargiz.chess.client.ui.BaseController;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class DialogController extends BaseController {

    @Override
    public void show() {
        this.stage = new Stage();
        this.stage.setScene(scene);
        this.stage.centerOnScreen();
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.initOwner(primaryStage);
        this.stage.show();
    }

    @Override
    public void navigateTo(BaseController controller) {
        super.navigateTo(controller);
        stage.close();
    }
}
