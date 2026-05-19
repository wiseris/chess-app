package com.nargiz.chess.client.process.impl;

import com.nargiz.chess.client.model.events.UpdateGameStateEvent;
import com.nargiz.chess.client.process.ClientCommandProcessor;
import com.nargiz.chess.shared.command.UpdateGameState;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;

@Component
public class UpdateGameStateProcessor implements ClientCommandProcessor<UpdateGameState> {
    @Inject
    ApplicationEventBus eventBus;

    @Override
    public void processCommand(UpdateGameState command) {
        eventBus.publish(UpdateGameStateEvent.builder()
                        .figures(command.getFigures())
                        .historyData(command.getHistoryData())
                .build());
        System.out.println("UpdateGameState received: " + command);
    }

    @Override
    public Class<UpdateGameState> getCommandClass() {
        return UpdateGameState.class;
    }
}
