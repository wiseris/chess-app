package com.nargiz.chess.client.ui.dialogs;

import com.nargiz.chess.client.network.TCPClient;
import com.nargiz.chess.client.ui.screens.SeekerLobbyController;
import com.nargiz.chess.client.ui.screens.ServerListController;
import com.nargiz.chess.shared.command.JoinLobby;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import com.nargiz.chess.shared.models.FigureData;
import com.nargiz.chess.shared.models.ServerInfo;
import com.nargiz.chess.shared.models.enums.FigureType;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class DialogSelectTransformationController extends DialogController {

    FigureType selected = FigureType.QUEEN;

    @Override
    protected String getFormUrl() {
        return "/forms/SelectTransformation.fxml";
    }

    @PostConstruct
    @Override
    public void loadScene() {
        super.loadScene();
    }

    @FXML
    public void selectRook() {
        selected = FigureType.ROOK;
        stage.close();
    }

    @FXML
    public void selectKnight() {
        selected = FigureType.KNIGHT;
        stage.close();
    }

    @FXML
    public void selectBishop() {
        selected = FigureType.BISHOP;
        stage.close();
    }

    @FXML
    public void selectQueen() {
        selected = FigureType.QUEEN;
        stage.close();
    }

    public FigureType getSelected() {
        return selected;
    }

    @Override
    public void showAndWait() {
        this.stage = new Stage();
        this.stage.setScene(scene);
        this.stage.centerOnScreen();
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.initOwner(primaryStage);
        this.stage.showAndWait();
    }
}
