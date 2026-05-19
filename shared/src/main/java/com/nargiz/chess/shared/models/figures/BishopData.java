package com.nargiz.chess.shared.models.figures;

import com.nargiz.chess.shared.command.ActionCommand;
import com.nargiz.chess.shared.models.*;
import com.nargiz.chess.shared.models.actions.KillFigure;
import com.nargiz.chess.shared.models.actions.MoveFigure;
import com.nargiz.chess.shared.models.enums.FigureType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.awt.*;
import java.util.List;

import static com.nargiz.chess.shared.models.enums.FigureType.BISHOP;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
public class BishopData extends FigureData {

    {
       setFigureType(BISHOP);
    }

    @Override
    public ActionResult validateAction(GameData gameData, ActionCommand action) {
        ActionResult result = new ActionResult();
        CellPosition targetPosition = action.getToPosition();
        if (isOutOfBoard(targetPosition)) {
            result.addError(new ValidationError("Move out of board"));
            return result;
        }

        if (getPosition().equals(targetPosition) ||
                (!CellPosition.isSameDiagonal(getPosition(), targetPosition))
        ) {
            result.addError(new ValidationError("Impossible move"));
            return result;
        }

        FigureData attackedFigure = null;
        for(FigureData figure: gameData.getFigures().values()) {
            if (figure.isDead() || figure == this) continue;

            if ( !figure.getPosition().equals(targetPosition) && figure.isOpponentColor(gameData.getCurrentPlayer().getKing()) &&
                    figure.isUnderAttack(gameData, gameData.getCurrentPlayer().getKing().getPosition(), this, targetPosition)
            ) {
                result.addError(new ValidationError("King is under attack"));
                return result;
            }

            if (
                    figure.getPosition().isDiagonalBetween(getPosition(), targetPosition)
            ) {
                result.addError(new ValidationError("Impossible move"));
                return result;
            }

            if (figure.getColor().equals(getColor()) && figure.getPosition().equals(targetPosition)) {
                result.addError(new ValidationError("Target cell is occupied by ally"));
                return result;
            }

            if (!figure.getColor().equals(getColor()) && figure.getPosition().equals(targetPosition)) {
                attackedFigure = figure;
            }
        }
        if (attackedFigure != null) {
            result.addAction(new KillFigure(attackedFigure));
        }
        result.addAction(new MoveFigure(this, targetPosition));
        return result;
    }

    @Override
    public boolean isUnderAttack(GameData gameData, CellPosition targetPosition, FigureData exclude, CellPosition replacedPosition) {
        if (!CellPosition.isSameDiagonal(getPosition(), targetPosition)) {
            return false;
        }

        for(FigureData figure: gameData.getFigures().values()) {
            if (figure.isDead() || figure == this) continue;
            CellPosition figurePosition = figure.getPosition();
            if (figure == exclude) {
                figurePosition = replacedPosition;
            }

            if (figurePosition.isDiagonalBetween(getPosition(), targetPosition)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isLocked(GameData gameData) {
        CellPosition[] lockPositions = {
                getPosition().shift(-1, -1),
                getPosition().shift(1, 1),
                getPosition().shift(-1, 1),
                getPosition().shift(1, -1)
        };

        for (CellPosition lockPos: lockPositions) {
            if (FigureData.isOutOfBoard(lockPos)) {
                continue;
            }
            FigureData lockFigure = gameData.getFigureAt(lockPos);
            if (lockFigure == null || lockFigure.isOpponentColor(this)) {
                return false;
            }
        }
        return true;
    }
}
