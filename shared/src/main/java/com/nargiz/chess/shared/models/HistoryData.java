package com.nargiz.chess.shared.models;

import com.nargiz.chess.shared.models.enums.ActionType;
import com.nargiz.chess.shared.models.enums.ColorType;
import com.nargiz.chess.shared.models.enums.FigureType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HistoryData {
    private ActionType action;

    private ColorType color;
    private FigureType figure;
    private FigureType transform;
    private CellPosition from;
    private CellPosition to;

    private boolean shortCastling;
    private boolean longCastling;

    private FigureType killedFigure;
    private boolean enPassant;
    private boolean check;
    private boolean mate;
    private boolean draw;

    public String notation() {
        StringBuilder builder = new StringBuilder();
        if (isShortCastling() || isLongCastling()) {
            builder.append(from.notation());
            builder.append(to.notation());
            return builder.toString();
        }
        builder.append(convert(figure));
        builder.append(from.notation());
        if (ActionType.ATTACK.equals(action)) {
            builder.append("x");
        }
        builder.append(to.notation());
        if (transform != null) {
            builder.append(convert(transform));
        }
        if (isCheck()) {
            builder.append("+");
        }
        if (isMate()) {
            builder.append("#");
        }
        if (isEnPassant()) {
            builder.append("e. p.");
        }
        return builder.toString();
    }

    private Object convert(FigureType figure) {
        return switch (figure) {
            case KING -> 'K';
            case QUEEN -> 'Q';
            case ROOK -> 'R';
            case BISHOP -> 'B';
            case KNIGHT -> 'N';
            case PAWN -> "";
        };
    }
}
