package com.nargiz.chess.shared.models.actions;

import com.nargiz.chess.shared.models.ActionPerform;
import com.nargiz.chess.shared.models.FigureData;
import com.nargiz.chess.shared.models.GameData;
import com.nargiz.chess.shared.models.enums.FigureType;
import com.nargiz.chess.shared.models.figures.BishopData;
import com.nargiz.chess.shared.models.figures.KnightData;
import com.nargiz.chess.shared.models.figures.QueenData;
import com.nargiz.chess.shared.models.figures.RookData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class TransformFigure implements ActionPerform {
    FigureData figure;
    FigureType figureType;

    @Override
    public void doAction(GameData gameData) {
        gameData.getFigures().remove(figure.getPosition());
        FigureData newFigure = transformFigure();
        newFigure.setPosition(figure.getPosition());
        newFigure.setColor(figure.getColor());
        newFigure.setMoved(true);
        gameData.getFigures().put(newFigure.getPosition(), newFigure);
    }

    public FigureData transformFigure() {
        return switch (figureType) {
            case QUEEN -> new QueenData();
            case ROOK -> new RookData();
            case BISHOP -> new BishopData();
            case KNIGHT -> new KnightData();
            default -> null;
        };
    }
}
