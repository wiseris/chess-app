package com.nargiz.chess.client.ui.screens;

import com.nargiz.chess.client.model.events.LobbyCreatedEvent;
import com.nargiz.chess.client.model.events.UpdateMembersEvent;
import com.nargiz.chess.client.network.TCPClient;
import com.nargiz.chess.shared.command.StartGame;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.client.ui.BaseController;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import com.nargiz.chess.shared.models.MemberData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.UUID;

@Component
public class HostLobbyController extends BaseController {

    @Inject
    MainScreenController mainScreenController;

    @Inject
    GameController gameController;

    @Inject
    ApplicationEventBus applicationEventBus;

    @FXML
    Spinner memberCount;

    @FXML
    Label hostName;

    @FXML
    SpinnerValueFactory<Integer> maxPlayerCount;

    @FXML
    ListView<MemberData> membersList;

    @Inject
    TCPClient tcpClient;

    @Override
    protected String getFormUrl() {
        return "/forms/Lobby.fxml";
    }

    @PostConstruct
    @Override
    public void loadScene() {
        super.loadScene();
    }

    @FXML
    public void switchBack() {
        navigateTo(mainScreenController);
    }

    @FXML
    public void startGame() {
        MemberData memberData = membersList.getSelectionModel().getSelectedItem();
        if (memberData == null) {
            return;
        }
        tcpClient.send(new StartGame(memberData.getId()));
        navigateTo(gameController);
    }

    @FXML
    void initialize() {
        maxPlayerCount =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 20, 2, 1);

        memberCount.setValueFactory(maxPlayerCount);

        membersList.setCellFactory(lv -> new ListCell<MemberData>() {
            @Override
            protected void updateItem(MemberData memberData, boolean empty) {
                super.updateItem(memberData, empty);
                setText((empty || memberData == null) ? null : memberData.getName());
            }
        });
    }

    @PostConstruct
    private void initEvents() {
        applicationEventBus.subscribeOn(LobbyCreatedEvent.class, this::onLobbyCreatedEvent);
        applicationEventBus.subscribeOn(UpdateMembersEvent.class, this::onUpdateMembersEvent);
    }

    private void onUpdateMembersEvent(UpdateMembersEvent updateMembersEvent) {
        Platform.runLater(() -> {
            membersList.getItems().clear();
            updateMembersEvent.getMembers().forEach(membersList.getItems()::add);
        });
    }

    private void onLobbyCreatedEvent(LobbyCreatedEvent event) {
        Platform.runLater(() ->{
            hostName.setText(event.getHostName());
            maxPlayerCount.setValue(event.getMaxMemberCount());
        });
    }
}
