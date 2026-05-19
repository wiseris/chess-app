package com.nargiz.chess.server;

import com.nargiz.chess.server.network.DiscoveryResponder;
import com.nargiz.chess.server.network.TCPServer;
import com.nargiz.chess.server.network.impl.TCPServerImpl;
import com.nargiz.chess.shared.ioc.Container;
import com.nargiz.chess.shared.ioc.Context;

public class ServerLauncher {
    public static void main(String[] args) {
        Context context = Container.createContext("com.nargiz.chess");

//        DiscoveryResponder server = context.get(DiscoveryResponder.class);
        TCPServer server = context.get(TCPServer.class);
        System.out.println(server);
        server.start(8888);

//        server.start("Shadow Slave Lobby", 5);

    }
}
