package com.nargiz.chess.client.model.events;

import com.nargiz.chess.shared.events.GameEvent;
import com.nargiz.chess.shared.models.CellPosition;
import com.nargiz.chess.shared.models.FigureData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class StartGameEvent implements GameEvent {
    private UUID whiteUserId;
    private UUID blackUserId;
    private String whitePlayerName;
    private String blackPlayerName;
    private Collection<FigureData> figures;
}
