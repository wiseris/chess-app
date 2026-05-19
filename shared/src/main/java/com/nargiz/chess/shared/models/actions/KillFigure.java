package com.nargiz.chess.shared.models.actions;

import com.nargiz.chess.shared.models.ActionPerform;
import com.nargiz.chess.shared.models.FigureData;
import com.nargiz.chess.shared.models.GameData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class KillFigure implements ActionPerform {
    FigureData figure;
    boolean enPassant;

    public KillFigure(FigureData figure) {
        this(figure, false);
    }

    @Override
    public void doAction(GameData gameData) {
        figure.setDead(true);
        gameData.getFigures().remove(figure.getPosition());
    }
}
