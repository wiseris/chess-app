package com.nargiz.chess.client.ui.screens;

import com.nargiz.chess.client.model.events.ServerFoundEvent;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.client.services.ServerDiscovery;
import com.nargiz.chess.client.ui.BaseController;
import com.nargiz.chess.client.ui.dialogs.DialogJoinLobbyController;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import com.nargiz.chess.shared.models.ServerInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

@Component
public class ServerListController extends BaseController {

    @Inject
    MainScreenController mainScreenController;

    @Inject
    DialogJoinLobbyController joinLobbyController;

    @Inject
    ApplicationEventBus eventBus;

    @Inject
    ServerDiscovery serverDiscovery;

    @FXML
    TableView tableView;

    @FXML
    private TableColumn<ServerInfo, String> nameColumn;

    @FXML
    private TableColumn<ServerInfo, String> addressColumn;

    @FXML
    private TableColumn<ServerInfo, String> onlineColumn;

    @Override
    protected String getFormUrl() {
        return "/forms/ServerList.fxml";
    }

    @FXML
    public void initialize() {

        nameColumn.setReorderable(false);
        nameColumn.setCellValueFactory(this::getNameProperty);

        addressColumn.setReorderable(false);
        addressColumn.setCellValueFactory(this::getAddressProperty);

        onlineColumn.setReorderable(false);
        onlineColumn.setCellValueFactory(this::getOnlineProperty);

        nameColumn.prefWidthProperty().bind(
                tableView.widthProperty()
                        .subtract(addressColumn.getWidth())
                        .subtract(onlineColumn.getWidth())
                        .subtract(20)
        );

//        tableView.getItems().add(new ServerInfo("test1", "11111", 8888, 1, 2));
//        tableView.getItems().add(new ServerInfo("test2", "1133411", 8888, 3, 5));
//        tableView.getItems().add(new ServerInfo("test3", "1121111", 8888, 2, 10));

        eventBus.subscribeOn(ServerFoundEvent.class, this::onServerFound);
    }

    public void onServerFound(ServerFoundEvent event) {
        tableView.getItems().add(event.getServer());
    }

    private SimpleStringProperty getOnlineProperty(TableColumn.CellDataFeatures<ServerInfo, String> data) {
        return new SimpleStringProperty("%s / %s".formatted(
                data.getValue().getCurrentPlayers(), data.getValue().getMaxPlayers())
        );
    }

    private SimpleStringProperty getNameProperty(TableColumn.CellDataFeatures<ServerInfo, String> data) {
        return new SimpleStringProperty(data.getValue().getName());
    }

    private SimpleStringProperty getAddressProperty(TableColumn.CellDataFeatures<ServerInfo, String> data) {
        return new SimpleStringProperty(data.getValue().getAddress());
    }

    @PostConstruct
    @Override
    public void loadScene() {
        super.loadScene();
    }

    @FXML
    public void joinLobby() {
        ServerInfo serverInfo = (ServerInfo) tableView.getSelectionModel().getSelectedItem();
        if (serverInfo == null) {
            return;
        }
        joinLobbyController.selectServer(serverInfo);
        navigateTo(joinLobbyController);
    }

    @FXML
    public void switchBack() {
        navigateTo(mainScreenController);
    }

    @Override
    public void fireOnShow() {
        tableView.getItems().clear();
        serverDiscovery.findServers();
    }

    @Override
    public void fireOnHide() {
        serverDiscovery.stop();
    }
}
