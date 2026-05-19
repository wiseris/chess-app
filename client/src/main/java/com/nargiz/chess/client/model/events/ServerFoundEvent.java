package com.nargiz.chess.client.model.events;

import com.nargiz.chess.shared.models.ServerInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerFoundEvent implements com.nargiz.chess.shared.events.GameEvent {
    private ServerInfo server;
}
