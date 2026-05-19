package com.nargiz.chess.client.process.impl;

import com.nargiz.chess.client.model.events.ErrorEvent;
import com.nargiz.chess.client.model.events.StartGameEvent;
import com.nargiz.chess.client.process.ClientCommandProcessor;
import com.nargiz.chess.shared.command.response.ErrorResponse;
import com.nargiz.chess.shared.command.response.StartGameResponse;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import javafx.application.Platform;
import javafx.scene.control.Alert;

@Component
public class ErrorResponseProcessor implements ClientCommandProcessor<ErrorResponse> {
    @Inject
    ApplicationEventBus eventBus;

    @Override
    public void processCommand(ErrorResponse command) {
        Platform.runLater( () -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(command.getMessage());
            alert.showAndWait();
        });
        eventBus.publish(new ErrorEvent(command.getMessage()));
        System.out.println("ErrorResponse received: " + command);
    }

    @Override
    public Class<ErrorResponse> getCommandClass() {
        return ErrorResponse.class;
    }
}
