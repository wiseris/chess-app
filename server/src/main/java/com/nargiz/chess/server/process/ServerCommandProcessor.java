package com.nargiz.chess.server.process;

import com.nargiz.chess.shared.command.ChessCommand;
import com.nargiz.chess.shared.command.response.ChessCommandResponse;

public interface ServerCommandProcessor<C extends ChessCommand,R extends ChessCommandResponse> {
    R processCommand(C command);
    Class<C> getCommandClass();
}
