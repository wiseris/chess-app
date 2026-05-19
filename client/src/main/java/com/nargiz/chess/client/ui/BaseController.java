package com.nargiz.chess.client.ui;

import com.nargiz.chess.client.model.events.ErrorEvent;
import com.nargiz.chess.client.model.events.LobbyCreatedEvent;
import com.nargiz.chess.client.model.events.UpdateMembersEvent;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class BaseController {
    protected Parent root;
    protected Stage stage;
    protected Scene scene;

    protected boolean active;
    
    @Inject
    protected Stage primaryStage;

    @Inject
    ApplicationEventBus applicationEventBus;

    protected abstract String getFormUrl();

    public void loadScene() {
        try {
            String fxmlPath = getFormUrl();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setController(this);
            this.root = loader.load();
            this.scene = new Scene(root);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + getFormUrl(), e);
        }
    }

    public void navigateTo(BaseController controller) {
        this.hide();
        controller.show();
    }

    public void navigateToAndWait(BaseController controller) {
        controller.showAndWait();
    }

    public void showAndWait() {

    }

    public void show() {
        active = true;
        this.stage = primaryStage;
        boolean wasMaximized = stage.isMaximized();
        this.stage.setScene(scene);
        this.stage.setMaximized(!wasMaximized);
        this.stage.setMaximized(wasMaximized);
        fireOnShow();
    }

    protected void hide() {
        active = false;
        fireOnHide();
    }

    public Parent getRoot() {
        return root;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void removeObjectById(String id) {
        Node node = root.lookup(id);
        if (node != null) {
            ((Pane)node.getParent()).getChildren().remove(node);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void fireOnShow() {

    }

    public void fireOnHide() {

    }

}
