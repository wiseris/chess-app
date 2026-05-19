package com.nargiz.chess.client.process.impl;

import com.nargiz.chess.client.model.events.JoinLobbyEvent;
import com.nargiz.chess.client.model.events.StartGameEvent;
import com.nargiz.chess.client.process.ClientCommandProcessor;
import com.nargiz.chess.shared.command.response.JoinLobbyResponse;
import com.nargiz.chess.shared.command.response.StartGameResponse;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;

@Component
public class StartGameResponseProcessor implements ClientCommandProcessor<StartGameResponse> {
    @Inject
    ApplicationEventBus eventBus;

    @Override
    public void processCommand(StartGameResponse command) {
        eventBus.publish(StartGameEvent.builder()
                        .whiteUserId(command.getWhiteUserId())
                        .blackUserId(command.getBlackUserId())
                        .whitePlayerName(command.getWhitePlayerName())
                        .blackPlayerName(command.getBlackPlayerName())
                        .figures(command.getFigures())
                .build());
        System.out.println("StartGameResponse received: " + command);
    }

    @Override
    public Class<StartGameResponse> getCommandClass() {
        return StartGameResponse.class;
    }
}
