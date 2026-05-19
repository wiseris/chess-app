package com.nargiz.chess.client.process;

import com.nargiz.chess.shared.command.ChessCommand;
import com.nargiz.chess.shared.command.response.ChessCommandResponse;

public interface ClientCommandProcessor<C extends ChessCommand> {
    void processCommand(C command);
    Class<C> getCommandClass();
}
