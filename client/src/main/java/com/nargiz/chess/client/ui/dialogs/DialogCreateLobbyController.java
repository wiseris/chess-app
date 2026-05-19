package com.nargiz.chess.client.ui.dialogs;

import com.nargiz.chess.client.network.TCPClient;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.client.ui.screens.HostLobbyController;
import com.nargiz.chess.client.ui.screens.MainScreenController;
import com.nargiz.chess.server.network.DiscoveryResponder;
import com.nargiz.chess.server.network.TCPServer;
import com.nargiz.chess.shared.command.CreateLobby;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import com.nargiz.chess.shared.models.ServerInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

@Component
public class DialogCreateLobbyController extends DialogController {

    @Inject
    ApplicationEventBus applicationEventBus;

    @Inject
    HostLobbyController hostLobbyController;

    @Inject
    MainScreenController mainScreenController;

    @Inject
    DiscoveryResponder discoveryResponder;

    @FXML
    Spinner memberCount;

    @FXML
    TextField lobbyName;

    @FXML
    TextField name;

    @Inject
    TCPServer server;

    @Inject
    TCPClient client;

    SpinnerValueFactory<Integer> maxPlayerCount;

    @Override
    protected String getFormUrl() {
        return "/forms/CreateLobby.fxml";
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
    public void createLobby() {
        ServerInfo serverInfo = ServerInfo.builder()
                .address("127.0.0.1")
                .port(8888)
                .build();
        CreateLobby createLobby = CreateLobby.builder()
                .lobbyName(lobbyName.getText())
                .memberName(name.getText())
                .maxPlayerCount(maxPlayerCount.getValue())
                .build();

        server.start(8888)
           .thenCompose(result -> client.start(serverInfo))
           .thenRun(() -> client.send(createLobby))
           .thenRun(() -> discoveryResponder.start());
        navigateTo(hostLobbyController);
    }

    @FXML
    void initialize() {
        maxPlayerCount =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 20, 2, 1);

        memberCount.setValueFactory(maxPlayerCount);

//        applicationEventBus.subscribeOn();
    }
}
