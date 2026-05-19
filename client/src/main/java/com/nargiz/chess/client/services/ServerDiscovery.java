package com.nargiz.chess.client.services;

import com.nargiz.chess.shared.models.ServerInfo;

import java.util.function.Consumer;

public interface ServerDiscovery {
    void findServers();

    void stop();
}
