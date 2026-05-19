package com.nargiz.chess.server.process.impl;

import com.nargiz.chess.server.process.ServerCommandProcessor;
import com.nargiz.chess.server.service.GameService;
import com.nargiz.chess.shared.command.ConnectServer;
import com.nargiz.chess.shared.command.CreateLobby;
import com.nargiz.chess.shared.command.response.ConnectServerResponse;
import com.nargiz.chess.shared.command.response.CreateLobbyResponse;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;

@Component
public class CreateLobbyProcessor implements ServerCommandProcessor<CreateLobby, CreateLobbyResponse> {
    @Inject
    GameService gameService;

    @Override
    public CreateLobbyResponse processCommand(CreateLobby command) {
        gameService.createLobby(
                command.getUserId(),
                command.getLobbyName(),
                command.getMemberName(),
                command.getMaxPlayerCount()
        );

        return CreateLobbyResponse.builder()
                .userId(command.getUserId())
                .hostName(command.getMemberName())
                .maxMemberCount(command.getMaxPlayerCount())
                .message("Lobby is created")
                .build();
    }

    @Override
    public Class<CreateLobby> getCommandClass() {
        return CreateLobby.class;
    }
}
