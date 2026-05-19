package com.nargiz.chess.client.process.impl;

import com.nargiz.chess.client.process.ClientCommandProcessor;
import com.nargiz.chess.shared.command.ConnectServer;
import com.nargiz.chess.shared.command.response.ConnectServerResponse;
import com.nargiz.chess.shared.ioc.anotation.Component;

@Component
public class ConnectServerResponseProcessor implements ClientCommandProcessor<ConnectServerResponse> {
    @Override
    public void processCommand(ConnectServerResponse command) {
        System.out.println("ConnectServerResponse received: " + command);
    }

    @Override
    public Class<ConnectServerResponse> getCommandClass() {
        return ConnectServerResponse.class;
    }
}
