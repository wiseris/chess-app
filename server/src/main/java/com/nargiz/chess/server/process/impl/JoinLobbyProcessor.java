package com.nargiz.chess.server.process.impl;

import com.nargiz.chess.server.process.ServerCommandProcessor;
import com.nargiz.chess.server.service.GameService;
import com.nargiz.chess.shared.command.JoinLobby;
import com.nargiz.chess.shared.command.response.JoinLobbyResponse;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;

@Component
public class JoinLobbyProcessor implements ServerCommandProcessor<JoinLobby, JoinLobbyResponse> {
    @Inject
    GameService gameService;

    @Override
    public JoinLobbyResponse processCommand(JoinLobby command) {
        gameService.connect(
                command.getHostId(),
                command.getUserId(),
                command.getMemberName()
        );

        return JoinLobbyResponse.builder()
                .userId(command.getUserId())
                .hostName(gameService.getHostName(command.getHostId()))
                .build();
    }

    @Override
    public Class<JoinLobby> getCommandClass() {
        return JoinLobby.class;
    }
}
