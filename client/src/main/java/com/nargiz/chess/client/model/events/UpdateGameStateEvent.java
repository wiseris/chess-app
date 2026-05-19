package com.nargiz.chess.client.model.events;

import com.nargiz.chess.shared.events.GameEvent;
import com.nargiz.chess.shared.models.FigureData;
import com.nargiz.chess.shared.models.HistoryData;
import com.nargiz.chess.shared.models.MemberData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class UpdateGameStateEvent implements GameEvent {
    Collection<FigureData> figures;
    Collection<HistoryData> historyData;
}
