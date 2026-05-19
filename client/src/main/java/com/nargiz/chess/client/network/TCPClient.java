package com.nargiz.chess.client.network;

import com.nargiz.chess.shared.command.ChessCommand;
import com.nargiz.chess.shared.models.ServerInfo;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TCPClient {
    void send(ChessCommand commandResponse);
    CompletableFuture<Void> start(ServerInfo serverInfo);
    void stop();
    UUID getUserId();
}
