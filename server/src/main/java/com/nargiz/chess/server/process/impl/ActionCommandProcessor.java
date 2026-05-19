package com.nargiz.chess.server.process.impl;

import com.nargiz.chess.server.process.ServerCommandProcessor;
import com.nargiz.chess.server.service.GameService;
import com.nargiz.chess.shared.command.ActionCommand;
import com.nargiz.chess.shared.command.StartGame;
import com.nargiz.chess.shared.command.UpdateGameState;
import com.nargiz.chess.shared.command.response.ErrorResponse;
import com.nargiz.chess.shared.command.response.StartGameResponse;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;

@Component
public class ActionCommandProcessor implements ServerCommandProcessor<ActionCommand, ErrorResponse> {
    @Inject
    GameService gameService;

    @Override
    public ErrorResponse processCommand(ActionCommand command) {
        gameService.performGameAction(command);

        return null;
    }

    @Override
    public Class<ActionCommand> getCommandClass() {
        return ActionCommand.class;
    }
}
