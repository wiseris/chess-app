package com.nargiz.chess.server.process.impl;

import com.nargiz.chess.server.process.ServerCommandProcessor;
import com.nargiz.chess.shared.command.ConnectServer;
import com.nargiz.chess.shared.command.response.ConnectServerResponse;
import com.nargiz.chess.shared.ioc.anotation.Component;

@Component
public class ConnectServerProcessor implements ServerCommandProcessor<ConnectServer, ConnectServerResponse> {
    @Override
    public ConnectServerResponse processCommand(ConnectServer command) {
        return ConnectServerResponse.builder()
                .allowed(true)
                .message("Welcome on board")
                .build();
    }

    @Override
    public Class<ConnectServer> getCommandClass() {
        return ConnectServer.class;
    }
}
