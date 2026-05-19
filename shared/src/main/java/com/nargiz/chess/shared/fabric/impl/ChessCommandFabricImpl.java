package com.nargiz.chess.shared.fabric.impl;

import com.nargiz.chess.shared.fabric.ChessCommandFabric;
import com.nargiz.chess.shared.exceptions.ChessCommandNotFound;
import com.nargiz.chess.shared.ioc.Container;
import com.nargiz.chess.shared.ioc.anotation.Command;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChessCommandFabricImpl implements ChessCommandFabric {
    Map<String, Class<? extends Command>> commandsMap = new ConcurrentHashMap<>();

    @Override
    public Class<? extends Command> getCommand(String command) {
        Class<? extends Command> result = commandsMap.get(command);
        if (result == null) {
            throw new ChessCommandNotFound(command);
        }
        return result;
    }

    @PostConstruct
    private void collectCommands() {
        List<Class<?>> commands = Container.scanComponents("com.nargiz.chess.shared.command", Command.class);
        for(Class<?> command: commands) {
            commandsMap.put(command.getSimpleName(), (Class<? extends Command>) command);
        }
    }
}
