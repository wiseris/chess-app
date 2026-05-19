package com.nargiz.chess.client.process.impl;

import com.nargiz.chess.client.model.events.JoinLobbyEvent;
import com.nargiz.chess.client.process.ClientCommandProcessor;
import com.nargiz.chess.shared.command.response.JoinLobbyResponse;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;

@Component
public class JoinLobbyResponseProcessor implements ClientCommandProcessor<JoinLobbyResponse> {
    @Inject
    ApplicationEventBus eventBus;

    @Override
    public void processCommand(JoinLobbyResponse command) {
        eventBus.publish(JoinLobbyEvent.builder()
                        .userId(command.getUserId())
                        .hostName(command.getHostName())
                .build());
        System.out.println("JoinLobbyResponse received: " + command);
    }

    @Override
    public Class<JoinLobbyResponse> getCommandClass() {
        return JoinLobbyResponse.class;
    }
}
