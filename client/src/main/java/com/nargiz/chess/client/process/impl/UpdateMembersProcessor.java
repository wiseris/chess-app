package com.nargiz.chess.client.process.impl;

import com.nargiz.chess.client.model.events.UpdateMembersEvent;
import com.nargiz.chess.client.process.ClientCommandProcessor;
import com.nargiz.chess.shared.command.UpdateMembers;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;

@Component
public class UpdateMembersProcessor implements ClientCommandProcessor<UpdateMembers> {
    @Inject
    ApplicationEventBus eventBus;

    @Override
    public void processCommand(UpdateMembers command) {
        eventBus.publish(UpdateMembersEvent.builder()
                        .members(command.getMembers())
                .build());
        System.out.println("UpdateMembers received: " + command);
    }

    @Override
    public Class<UpdateMembers> getCommandClass() {
        return UpdateMembers.class;
    }
}
