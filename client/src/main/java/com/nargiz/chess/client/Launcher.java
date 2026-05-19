package com.nargiz.chess.client;

import com.nargiz.chess.shared.events.ApplicationStopEvent;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.client.ui.screens.MainScreenController;
import com.nargiz.chess.shared.ioc.Container;
import com.nargiz.chess.shared.ioc.Context;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Launcher extends Application {

    ApplicationEventBus eventBus;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest(this::stopRequest);
        Context context = Container.createContext("com.nargiz.chess");
        context.addNewInstance(Stage.class, primaryStage);

        MainScreenController mainScreen = context.get(MainScreenController.class);
        eventBus = context.get(ApplicationEventBus.class);

        primaryStage.setTitle("Chess game");

        primaryStage.show();
        primaryStage.setMaximized(true);

        mainScreen.show();
    }

    public static void main(String[] args) {
        launch(args);
//        Application.launch(ChessUI.class, args);
//        Context context = Container.createContext("com.nargiz.chess");
//
//        MainScreenController mainScreen = context.get(MainScreenController.class);
//
//        mainScreen.loadForm();
//        ServerDiscovery server = context.get(ServerDiscovery.class);
//
//        server.findServers(serverInfo -> {
//            System.out.println("New server found: " + serverInfo);
//        }, () -> {
//            System.out.println("Discovery is ended");
//        });
//
//        try {
//            Thread.sleep(discoveryDurationSeconds * 1000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
    }

    public void stopRequest(WindowEvent event) {
        eventBus.publish(new ApplicationStopEvent());
    }
}
