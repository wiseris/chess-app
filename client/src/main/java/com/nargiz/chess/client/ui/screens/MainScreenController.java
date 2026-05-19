package com.nargiz.chess.client.ui.screens;

import com.nargiz.chess.client.network.TCPClient;
import com.nargiz.chess.client.ui.BaseController;
import com.nargiz.chess.client.ui.dialogs.DialogCreateLobbyController;
import com.nargiz.chess.server.network.TCPServer;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import com.nargiz.chess.shared.models.ServerInfo;
import javafx.fxml.FXML;

@Component
public class MainScreenController extends BaseController {

    @Inject
    ServerListController serverList;

    @Inject
    DialogCreateLobbyController createLobbyController;

    @Override
    protected String getFormUrl() {
        return "/forms/MenuScreen.fxml";
    }

    @PostConstruct
    @Override
    public void loadScene() {
        super.loadScene();
    }

    @FXML
    public void joinGame() {
        navigateTo(serverList);
    }

    @FXML
    public void hostGame() {
        navigateTo(createLobbyController);
    }
}
