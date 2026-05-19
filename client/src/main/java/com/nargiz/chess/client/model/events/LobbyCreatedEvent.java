package com.nargiz.chess.client.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class LobbyCreatedEvent implements com.nargiz.chess.shared.events.GameEvent {
    private UUID userId;
    private String hostName;
    private int maxMemberCount;
    private String message;
}
