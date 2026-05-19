package com.nargiz.chess.client.process.impl;

import com.nargiz.chess.client.model.events.LobbyCreatedEvent;
import com.nargiz.chess.client.process.ClientCommandProcessor;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.command.response.CreateLobbyResponse;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;

@Component
public class CreateLobbyResponseProcessor implements ClientCommandProcessor<CreateLobbyResponse> {
    @Inject
    ApplicationEventBus eventBus;

    @Override
    public void processCommand(CreateLobbyResponse command) {
        eventBus.publish(LobbyCreatedEvent.builder()
                        .userId(command.getUserId())
                        .hostName(command.getHostName())
                        .maxMemberCount(command.getMaxMemberCount())
                .build());
        System.out.println("CreateLobbyResponse received: " + command);
    }

    @Override
    public Class<CreateLobbyResponse> getCommandClass() {
        return CreateLobbyResponse.class;
    }
}
