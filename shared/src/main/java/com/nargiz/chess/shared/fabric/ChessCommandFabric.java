package com.nargiz.chess.shared.fabric;

import com.nargiz.chess.shared.ioc.anotation.Command;

public interface ChessCommandFabric {
    Class<? extends Command> getCommand(String command);
}
