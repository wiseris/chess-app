package com.nargiz.chess.shared.models.actions;

import com.nargiz.chess.shared.models.ActionPerform;
import com.nargiz.chess.shared.models.CellPosition;
import com.nargiz.chess.shared.models.FigureData;
import com.nargiz.chess.shared.models.GameData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class MoveFigure implements ActionPerform {
    FigureData figure;
    CellPosition targetPosition;

    @Override
    public void doAction(GameData gameData) {
        gameData.getFigures().remove(figure.getPosition());
        figure.setPosition(targetPosition);
        gameData.getFigures().put(targetPosition, figure);
        figure.setMoved(true);
    }
}
