package com.nargiz.chess.server.network.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nargiz.chess.server.exceptions.ServiceException;
import com.nargiz.chess.server.network.ClientHandler;
import com.nargiz.chess.server.process.ServerCommandProcessor;
import com.nargiz.chess.shared.command.ChessCommand;
import com.nargiz.chess.shared.command.Envelope;
import com.nargiz.chess.shared.command.response.ChessCommandResponse;
import com.nargiz.chess.shared.command.response.ErrorResponse;
import com.nargiz.chess.shared.fabric.ChessCommandFabric;

import java.nio.charset.StandardCharsets;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static com.nargiz.chess.shared.utils.Constants.NETWORK_TIMEOUT;

public class ClientHandlerImpl implements ClientHandler {
    private final Map<String, Object> BODY_OBJECT = new HashMap<>();
    private final UUID userId = UUID.randomUUID();
    private final Socket clientSocket;
    private PrintWriter writer;
    private final ObjectMapper mapper = new ObjectMapper();

    private ChessCommandFabric commandFabric;
    private Map<Class<? extends ServerCommandProcessor>, ServerCommandProcessor> processorsMap;

    private boolean running = true;

    private Consumer<UUID> onStop;

    public ClientHandlerImpl(Socket socket) {
        this.clientSocket = socket;
        try {
            socket.setSoTimeout(NETWORK_TIMEOUT);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        String clientId = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
        System.out.println("Processing %s in thread %s".formatted(clientId, Thread.currentThread().getName()));

        try (clientSocket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            this.writer = writer;
            while (running) {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }

                    System.out.println("User: %s  Received: %s".formatted(userId, line));


                    Envelope request = mapper.readValue(line, Envelope.class);

                    ChessCommand command = (ChessCommand) mapper.convertValue(
                            request.getBody(),
                            commandFabric.getCommand(request.getCommand())
                    );
                    command.setUserId(userId);

                    try {
                        ChessCommandResponse commandResponse = processCommand(command);
                        if (commandResponse != null) {
                            send(commandResponse);
                        }
                    } catch (ServiceException e) {
                        send(new ErrorResponse(e.getMessage()));
                    }

                } catch (SocketTimeoutException e) {
                    continue;
                } catch (Exception e) {
                    System.out.println("JSON parsing error: %s".formatted(e.getMessage()));
                }
            }

        } catch (IOException e) {
            System.out.println("Client handler error: %s".formatted(e.getMessage()));
        } finally {
            onStop.accept(userId);
        }
        System.out.println("Client disconnected: %s".formatted(clientSocket.getRemoteSocketAddress()));
    }

    @Override
    public void send(ChessCommand command) {
        Map<String, Object> commandBody = mapper.convertValue(command, BODY_OBJECT.getClass());

        Envelope response = new Envelope(command.getClass().getSimpleName(), commandBody);

        try {
            String commandJson = mapper.writeValueAsString(response);
            writer.println(commandJson);
            System.out.println("User: %s  Sent: %s".formatted(userId, commandJson));
        } catch (JsonProcessingException e) {
            System.err.println("Command parsing error: " + e.getMessage());
        }
    }

    @Override
    public void setCommandFabric(ChessCommandFabric commandFabric) {
        this.commandFabric = commandFabric;
    }

    @Override
    public void setCommandProcessorMap(Map<Class<? extends ServerCommandProcessor>, ServerCommandProcessor> processorsMap) {
        this.processorsMap = processorsMap;
    }

    @Override
    public void listenStop(Consumer<UUID> stopConsumer) {
        onStop = stopConsumer;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public ChessCommandResponse processCommand(ChessCommand command) {
        ServerCommandProcessor processor = processorsMap.get(command.getClass());
        if (processor == null) {
            System.out.println("Processor not found: " + command.getClass());
        }
        ChessCommandResponse response = processor.processCommand(command);
        if (response != null) {
            response.setCommandId(command.getCommandId());
        }
        return response;
    }

    @Override
    public UUID getUserId() {
        return userId;
    }
}
