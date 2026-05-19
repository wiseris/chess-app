package com.nargiz.chess.client.network.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nargiz.chess.client.model.events.JoinLobbyEvent;
import com.nargiz.chess.client.model.events.LobbyCreatedEvent;
import com.nargiz.chess.shared.command.CreateLobby;
import com.nargiz.chess.shared.events.ApplicationStopEvent;
import com.nargiz.chess.client.network.TCPClient;
import com.nargiz.chess.client.process.ClientCommandProcessor;
import com.nargiz.chess.shared.command.ChessCommand;
import com.nargiz.chess.shared.command.Envelope;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.fabric.ChessCommandFabric;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import com.nargiz.chess.shared.models.ServerInfo;

import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nargiz.chess.shared.utils.Constants.NETWORK_TIMEOUT;

@Component
public class TCPClientImpl implements TCPClient {
    private final Map<String, Object> BODY_OBJECT = new HashMap<>();
    private ObjectMapper mapper = new ObjectMapper();
    private PrintWriter out;
    private ServerInfo serverInfo;
    private boolean running;

    private UUID userId;

    @Inject
    private ChessCommandFabric commandFabric;

    @Inject
    private List<ClientCommandProcessor> processors;

    @Inject
    private ApplicationEventBus eventBus;

    private Map<Class<? extends ClientCommandProcessor>, ClientCommandProcessor> processorsMap;


    @Override
    public CompletableFuture<Void> start(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        CompletableFuture<Void> signal = new CompletableFuture<>();
        new Thread(() -> process(signal)).start();
        return signal;
    }

    private void process(CompletableFuture<Void> signal) {
        running = true;
        System.out.println("TCP Client is on");
        System.out.println("Connecting to " + serverInfo.getAddress() + ":" + serverInfo.getPort());

        try (
                Socket socket = createSocketWithTimeout(serverInfo.getAddress(), serverInfo.getPort(), NETWORK_TIMEOUT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        ) {
            System.out.println("Connected to server!");
            this.out = out;
            signal.complete(null);
            while (running) {
                String line = null;
                try {
                    line = in.readLine();
                    if (line == null) {
                        break;
                    }
                } catch (SocketTimeoutException e) {
                    continue;
                }
                System.out.println("Received: %s".formatted(line));

                Envelope request = mapper.readValue(line, Envelope.class);

                ChessCommand command = (ChessCommand) mapper.convertValue(
                        request.getBody(),
                        commandFabric.getCommand(request.getCommand())
                );

                ClientCommandProcessor processor = processorsMap.get(command.getClass());
                System.out.println("Processor:" + processor);
                if (processor != null) {
                    processor.processCommand(command);
                }

            }

        } catch (Exception e) {
            signal.completeExceptionally(e);
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            running = false;
        }
    }

    public void send(ChessCommand commandResponse) {
        if (!running) {
            System.err.println("Client is not connected");
            return;
        }

        Map<String, Object> responseBody = mapper.convertValue(commandResponse, BODY_OBJECT.getClass());

        Envelope response = new Envelope(commandResponse.getClass().getSimpleName(), responseBody);

        String responseJson = null;
        try {
            responseJson = mapper.writeValueAsString(response);
            out.println(responseJson);
            System.out.println("Sent: %s".formatted(responseJson));
        } catch (JsonProcessingException e) {
            System.err.println("Command parsing error: " + e.getMessage());
        }

    }

    private Socket createSocketWithTimeout(String address, int port, int timeout) throws IOException {
        Socket socket = new Socket(address, port);
        socket.setSoTimeout(timeout);
        return socket;
    }

    @Override
    public void stop() {
        running = false;
    }

    @PostConstruct
    public void init() {
        System.out.println("Client processors found: " + processors);

        processorsMap = processors.stream()
                .collect(
                        Collectors.toMap(
                                ClientCommandProcessor::getCommandClass,
                                Function.identity()
                        )
                );

        eventBus.subscribeOn(ApplicationStopEvent.class, this::onApplicationStop);
        eventBus.subscribeOn(LobbyCreatedEvent.class, this::onLobbyCreated);
        eventBus.subscribeOn(JoinLobbyEvent.class, this::onJoinLobby);

        System.out.println("Client processors map: " + processorsMap);
    }

    private void onJoinLobby(JoinLobbyEvent event) {
        userId = event.getUserId();
    }

    private void onLobbyCreated(LobbyCreatedEvent event) {
        userId = event.getUserId();
    }

    public UUID getUserId() {
        return userId;
    }

    private void onApplicationStop(ApplicationStopEvent event) {
        stop();
    }
}
