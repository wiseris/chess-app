package com.nargiz.chess.client.model.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorEvent implements com.nargiz.chess.shared.events.GameEvent {
    private String message;
}
