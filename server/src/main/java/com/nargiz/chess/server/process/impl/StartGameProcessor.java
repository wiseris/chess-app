package com.nargiz.chess.server.process.impl;

import com.nargiz.chess.server.exceptions.ServiceException;
import com.nargiz.chess.server.process.ServerCommandProcessor;
import com.nargiz.chess.server.service.GameService;
import com.nargiz.chess.shared.command.JoinLobby;
import com.nargiz.chess.shared.command.StartGame;
import com.nargiz.chess.shared.command.response.JoinLobbyResponse;
import com.nargiz.chess.shared.command.response.StartGameResponse;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.models.GameData;
import com.nargiz.chess.shared.models.LobbyData;

import java.util.ArrayList;

@Component
public class StartGameProcessor implements ServerCommandProcessor<StartGame, StartGameResponse> {
    @Inject
    GameService gameService;

    @Override
    public StartGameResponse processCommand(StartGame command) {
        if (!gameService.isHost(command.getUserId())) {
             throw new ServiceException("Access denied");
        }

        gameService.startGame(command.getMemberId());

        return null;
    }

    @Override
    public Class<StartGame> getCommandClass() {
        return StartGame.class;
    }
}
