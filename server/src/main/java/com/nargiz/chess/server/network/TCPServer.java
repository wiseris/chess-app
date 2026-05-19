package com.nargiz.chess.server.network;

import com.nargiz.chess.shared.command.ChessCommand;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TCPServer {
    void broadcast(Set<UUID> members, ChessCommand command);
    CompletableFuture<Void> start(int port);
    void stop();
}
