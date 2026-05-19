package com.nargiz.chess.server.network;

import com.nargiz.chess.server.process.ServerCommandProcessor;
import com.nargiz.chess.shared.command.ChessCommand;
import com.nargiz.chess.shared.command.response.ChessCommandResponse;
import com.nargiz.chess.shared.fabric.ChessCommandFabric;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface ClientHandler extends Runnable {
    ChessCommandResponse processCommand(ChessCommand command);

    void send(ChessCommand command);
    void setCommandFabric(ChessCommandFabric commandFabric);
    void setCommandProcessorMap(Map<Class<? extends ServerCommandProcessor>, ServerCommandProcessor> processorsMap);

    UUID getUserId();
    void stop();

    void listenStop(Consumer<UUID> stopConsumer);
}
