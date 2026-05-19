package com.nargiz.chess.server.events;

import com.nargiz.chess.shared.events.GameEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class MemberDisconnectedEvent implements GameEvent {
    private UUID memberId;
}
