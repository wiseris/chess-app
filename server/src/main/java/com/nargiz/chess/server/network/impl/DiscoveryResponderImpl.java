package com.nargiz.chess.server.network.impl;

import com.nargiz.chess.server.network.DiscoveryResponder;
import com.nargiz.chess.server.repository.ServerRepository;
import com.nargiz.chess.shared.constants.NetworkConstants;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import com.nargiz.chess.shared.models.LobbyData;
import com.nargiz.chess.shared.models.ServerInfo;
import com.nargiz.chess.shared.utils.JsonUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import static com.nargiz.chess.shared.utils.Constants.NETWORK_TIMEOUT;

@Component
public class DiscoveryResponderImpl implements DiscoveryResponder {
    private DatagramSocket socket;
    private boolean running;
    Thread responderThread;

    @Inject
    ServerRepository serverRepository;

    @Inject
    ApplicationEventBus eventBus;

    @Override
    public synchronized void start() {
        responderThread = new Thread(() -> {
            try {
                socket = new DatagramSocket(NetworkConstants.DISCOVERY_PORT);
                socket.setSoTimeout(NETWORK_TIMEOUT);
                socket.setBroadcast(true);
                running = true;
                System.out.println("Discovery listening on port " + NetworkConstants.DISCOVERY_PORT);

                byte[] buffer = new byte[1024];

                while (running) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    try {
                        socket.receive(packet);
                    } catch (SocketTimeoutException e) {
                        continue;
                    }

                    String query = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

                    if (NetworkConstants.DISCOVERY_QUERY.equals(query)) {
                        System.out.println("Discovery query received");
                        System.out.println("Lobby set are: " + serverRepository.getLobbies());
                        serverRepository.getLobbies().forEach(
                            lobby -> sendResponse(lobby, packet)
                        );
                    }
                }
            } catch (Exception e) {
                if (running) e.printStackTrace();
            }
        });
        responderThread.start();
    }

    private void sendResponse(LobbyData lobbyData, DatagramPacket packet) {
        ServerInfo info = ServerInfo.builder()
                .name(lobbyData.getName())
                .hostId(lobbyData.getHost().getId())
                .port(8888)
                .currentPlayers(lobbyData.getMembers().size())
                .maxPlayers(lobbyData.getMaxMemberCount())
                .build();
        String json = JsonUtils.toJson(info);
        byte[] responseData = json.getBytes(StandardCharsets.UTF_8);

        DatagramPacket response = new DatagramPacket(
                responseData, responseData.length,
                packet.getAddress(), packet.getPort()
        );
        try {
            socket.send(response);
            System.out.println("Responded by: " + json);
        } catch (IOException e) {
            throw new RuntimeException("Cannot send server info", e);
        }
        System.out.println("Responded to " + packet.getAddress());
    }

    @Override
    public synchronized void stop() {
        running = false;
        if (socket != null) socket.close();
        if (responderThread != null) {
            try {
                responderThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @PostConstruct
    public void init(){
        eventBus.subscribeOn(com.nargiz.chess.shared.events.ApplicationStopEvent.class, this::onApplicationStop);
    }

    public void onApplicationStop(com.nargiz.chess.shared.events.ApplicationStopEvent event) {
        stop();
    }
}
