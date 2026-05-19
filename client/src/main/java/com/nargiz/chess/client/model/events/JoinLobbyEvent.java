package com.nargiz.chess.client.model.events;

import com.nargiz.chess.shared.events.GameEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class JoinLobbyEvent implements GameEvent {
    private UUID userId;
    private String hostName;
}
