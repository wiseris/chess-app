package com.nargiz.chess.server.network.impl;

import com.nargiz.chess.server.events.MemberDisconnectedEvent;
import com.nargiz.chess.server.network.ClientHandler;
import com.nargiz.chess.server.network.TCPServer;
import com.nargiz.chess.server.process.ServerCommandProcessor;
import com.nargiz.chess.shared.command.ChessCommand;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.events.ApplicationStopEvent;
import com.nargiz.chess.shared.fabric.ChessCommandFabric;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import com.nargiz.chess.shared.models.MemberData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nargiz.chess.shared.utils.Constants.NETWORK_TIMEOUT;

@Component
public class TCPServerImpl implements TCPServer {
    private boolean running = true;
    private Map<UUID, ClientHandler> clients = new ConcurrentHashMap<>();

    @Inject
    private ChessCommandFabric commandFabric;

    @Inject
    private List<ServerCommandProcessor> processors;

    @Inject
    private ApplicationEventBus eventBus;

    private Map<Class<? extends ServerCommandProcessor>, ServerCommandProcessor> processorsMap;

    @Override
    public void broadcast(Set<UUID> members, ChessCommand command) {
        members.forEach(memberId -> {
            ClientHandler handler = clients.get(memberId);
            if (handler != null) {
                handler.send(command);
            }
        });
    }

    @Override
    public CompletableFuture<Void> start(int port) {
        CompletableFuture<Void> signal = new CompletableFuture<>();
        new Thread(() -> {
            try (ExecutorService threadPool = Executors.newVirtualThreadPerTaskExecutor();
                 ServerSocket serverSocket = new ServerSocket(port)
            ) {
                serverSocket.setSoTimeout(NETWORK_TIMEOUT);
                System.out.printf("Advanced TCP server is up on: %d", port);
                signal.complete(null);
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("New member is registered: " + clientSocket.getInetAddress().getHostAddress());
                        ClientHandler clientHandler = new ClientHandlerImpl(clientSocket);
                        clientHandler.setCommandFabric(commandFabric);
                        clientHandler.setCommandProcessorMap(processorsMap);
                        clientHandler.listenStop(this::removeClient);
                        clients.put(clientHandler.getUserId(), clientHandler);
                        threadPool.submit(clientHandler);
                    } catch (SocketTimeoutException e) {
                        continue;
                    }
                }
            } catch (Exception e) {
                signal.completeExceptionally(e);
                System.err.printf("Server error: %s", e.getMessage());
            }
        }).start();
        return signal;
    }

    @Override
    public void stop() {
        clients.values().forEach(c -> c.stop());
        running = false;
    }

    private void removeClient(UUID id) {
        eventBus.publish(new MemberDisconnectedEvent(id));
        clients.remove(id);
    }

    @PostConstruct
    public void init() {
        processorsMap = processors.stream()
                .collect(
                        Collectors.toMap(
                            ServerCommandProcessor::getCommandClass,
                            Function.identity()
                        )
                );
        eventBus.subscribeOn(ApplicationStopEvent.class, this::onApplicationStop);
    }

    private void onApplicationStop(ApplicationStopEvent event) {
        stop();
    }
}
