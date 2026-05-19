package com.nargiz.chess.client.services.impl;

import com.nargiz.chess.client.model.events.*;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.client.services.ServerDiscovery;
import com.nargiz.chess.shared.constants.NetworkConstants;
import com.nargiz.chess.shared.events.ApplicationStopEvent;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import com.nargiz.chess.shared.models.ServerInfo;
import com.nargiz.chess.shared.utils.JsonUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import static com.nargiz.chess.shared.constants.NetworkConstants.discoveryDurationSeconds;

@Component
public class ServerDiscoveryImpl implements ServerDiscovery {
//    private final ExecutorService receiver = Executors.newSingleThreadExecutor();
//    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private DatagramSocket socket;
    private volatile boolean running;

    @Inject
    ApplicationEventBus eventBus;

    @Override
    public void findServers() {
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.setSoTimeout(2000);

            byte[] queryData = NetworkConstants.DISCOVERY_QUERY.getBytes(StandardCharsets.UTF_8);

            // Broadcast
            socket.send(new DatagramPacket(queryData, queryData.length,
                    InetAddress.getByName("255.255.255.255"), NetworkConstants.DISCOVERY_PORT));
            // Localhost for testing
            socket.send(new DatagramPacket(queryData, queryData.length,
                    InetAddress.getByName("127.0.0.1"), NetworkConstants.DISCOVERY_PORT));

            System.out.println("Discovery query sent");

            running = true;

            new Thread(() -> {
                eventBus.publish(new ServerDiscoveryStartEvent());
                long endTime = System.currentTimeMillis() + discoveryDurationSeconds * 1000;
                while (running && endTime > System.currentTimeMillis()) {
                    try {
                        byte[] buffer = new byte[4096];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        String json = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                        ServerInfo server = JsonUtils.fromJson(json, ServerInfo.class);
                        server.setAddress(packet.getAddress().getHostAddress());

                        eventBus.publish(new ServerFoundEvent(server));
                    } catch (SocketTimeoutException timeoutException) {
                        System.out.println("Timeout");
                    } catch (Exception e) {
                        if (running) {
                            eventBus.publish(new ErrorEvent("Discovery error"));
                            System.err.println("Receive error: " + e.getMessage());
                        }
                    }
                }
                eventBus.publish(new ServerDiscoveryStopEvent());
            }).start();

        } catch (Exception e) {
            eventBus.publish(new ErrorEvent("Discovery error"));
            System.err.println("Discovery error: " + e.getMessage());
            stop();
        }
    }

    @Override
    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public void onApplicationStop(ApplicationStopEvent event) {
        stop();
    }

    @PostConstruct
    public void init(){
        eventBus.subscribeOn(ApplicationStopEvent.class, this::onApplicationStop);
    }
}
