package com.nargiz.chess.client.ui.dialogs;

import com.nargiz.chess.client.network.TCPClient;
import com.nargiz.chess.client.ui.screens.SeekerLobbyController;
import com.nargiz.chess.client.ui.screens.ServerListController;
import com.nargiz.chess.server.network.TCPServer;
import com.nargiz.chess.shared.command.JoinLobby;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import com.nargiz.chess.shared.models.ServerInfo;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

@Component
public class DialogJoinLobbyController extends DialogController {

    @Inject
    ServerListController serverList;

    @Inject
    SeekerLobbyController lobby;

    @Inject
    TCPClient client;

    private boolean hostModeEnabled = true;
    private ServerInfo serverInfo;

    @FXML
    TextField memberName;

    @Override
    protected String getFormUrl() {
        return "/forms/JoinLobby.fxml";
    }

    @PostConstruct
    @Override
    public void loadScene() {
        super.loadScene();
    }

    @FXML
    public void switchBack() {
        stage.close();
    }

    @FXML
    public void joinLobby() {
        client.start(serverInfo)
                .thenRun(this::sendJoinCommand);
        navigateTo(lobby);
    }

    private void sendJoinCommand() {
        client.send(JoinLobby.builder()
                .hostId(serverInfo.getHostId())
                .memberName(memberName.getText())
                .build()
        );
    }

    public boolean isHostModeEnabled() {
        return hostModeEnabled;
    }

    public void setHostModeEnabled(boolean hostModeEnabled) {
        this.hostModeEnabled = hostModeEnabled;
    }

    public void selectServer(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }
}
