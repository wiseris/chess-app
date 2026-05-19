package com.nargiz.chess.shared.models;

import com.nargiz.chess.shared.models.enums.ColorType;
import com.nargiz.chess.shared.models.enums.GameState;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.nargiz.chess.shared.models.enums.ColorType.BLACK;
import static com.nargiz.chess.shared.models.enums.ColorType.WHITE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameData {
    private MemberData white;
    private MemberData black;
    private GameState state;
    private Map<CellPosition, FigureData> figures;
    @Builder.Default
    private List<HistoryData> history = new ArrayList<>();

    public MemberData getCurrentPlayer() {
        if (WHITE.equals(getCurrentColor())) {
            return white;
        }
        return black;
    }

    public MemberData getOpponentPlayer() {
        if (WHITE.equals(getCurrentColor())) {
            return black;
        }
        return white;
    }

    public ColorType getCurrentColor() {
        if (history == null || history.isEmpty()) {
            return WHITE;
        }
        if (WHITE.equals(history.getLast().getColor())) {
            return BLACK;
        }
        return WHITE;
    }

    public FigureData getFigureAt(CellPosition position) {
        return figures.get(position);
    }
}
