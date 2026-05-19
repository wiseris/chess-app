package com.nargiz.chess.client.ui.screens;

import com.nargiz.chess.client.model.events.JoinLobbyEvent;
import com.nargiz.chess.client.model.events.StartGameEvent;
import com.nargiz.chess.client.model.events.UpdateMembersEvent;
import com.nargiz.chess.client.ui.BaseController;
import com.nargiz.chess.shared.command.StartGame;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import com.nargiz.chess.shared.models.MemberData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

@Component
public class SeekerLobbyController extends BaseController {

    @Inject
    ApplicationEventBus eventBus;

    @Inject
    ServerListController serverList;

    @Inject
    GameController gameController;

    @FXML
    Button selectButton;

    @FXML
    HBox memberCountPane;

    @FXML
    Label hostName;

    @FXML
    ListView<MemberData> membersList;

    @Override
    protected String getFormUrl() {
        return "/forms/Lobby.fxml";
    }

    @PostConstruct
    @Override
    public void loadScene() {
        super.loadScene();
        removeObjectById("#selectButton");
        removeObjectById("#memberCountPane");
    }

    @FXML
    void initialize() {
        membersList.setCellFactory(lv -> new ListCell<MemberData>() {
            @Override
            protected void updateItem(MemberData memberData, boolean empty) {
                super.updateItem(memberData, empty);
                setText((empty || memberData == null) ? null : memberData.getName());
            }
        });
    }

    @FXML
    public void switchBack() {
        navigateTo(serverList);
    }

    @FXML
    public void startGame() {
        navigateTo(gameController);
    }

    private void onJoinLobbyEvent(JoinLobbyEvent event) {
        Platform.runLater(() ->{
            hostName.setText(event.getHostName());
        });
    }

    private void onUpdateMembersEvent(UpdateMembersEvent updateMembersEvent) {
        Platform.runLater(() -> {
            membersList.getItems().clear();
            updateMembersEvent.getMembers().forEach(membersList.getItems()::add);
        });
    }

    private void onStartGameEvent(StartGameEvent event) {
        Platform.runLater(() -> {
            navigateTo(gameController);
        });
    }

    @PostConstruct
    private void initEvents() {
        eventBus.subscribeOn(JoinLobbyEvent.class, this::onJoinLobbyEvent);
        eventBus.subscribeOn(UpdateMembersEvent.class, this::onUpdateMembersEvent);
        eventBus.subscribeOn(StartGameEvent.class, this::onStartGameEvent);
    }
}
