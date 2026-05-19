package com.nargiz.chess.shared.models.figures;

import com.nargiz.chess.shared.command.ActionCommand;
import com.nargiz.chess.shared.models.*;
import com.nargiz.chess.shared.models.actions.KillFigure;
import com.nargiz.chess.shared.models.actions.MoveFigure;
import com.nargiz.chess.shared.models.enums.FigureType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
public class KnightData extends FigureData {

    {
        setFigureType(FigureType.KNIGHT);
    }

    @Override
    public ActionResult validateAction(GameData gameData, ActionCommand action) {
        ActionResult result = new ActionResult();
        CellPosition targetPosition = action.getToPosition();
        if (isOutOfBoard(targetPosition)) {
            result.addError(new ValidationError("Move out of board"));
            return result;
        }

        if (!isVerticalJump(targetPosition) && !isHorizontalJump(targetPosition)) {
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

    private boolean isVerticalJump(CellPosition targetPosition) {
        return Math.abs(getPosition().getRow() - targetPosition.getRow()) == 2
                && Math.abs(getPosition().getColumn() - targetPosition.getColumn()) == 1;
    }

    private boolean isHorizontalJump(CellPosition targetPosition) {
        return Math.abs(getPosition().getColumn() - targetPosition.getColumn()) == 2
                && Math.abs(getPosition().getRow() - targetPosition.getRow()) == 1;
    }

    @Override
    public boolean isUnderAttack(GameData gameData, CellPosition targetPosition, FigureData exclude, CellPosition replacedPosition) {
        return isVerticalJump(targetPosition) || isHorizontalJump(targetPosition);
    }

    @Override
    public boolean isLocked(GameData gameData) {
        CellPosition[] lockPositions = {
                getPosition().shift(-2, -1),
                getPosition().shift(-2, 1),
                getPosition().shift(-1, 2),
                getPosition().shift(-1, -2),
                getPosition().shift(2, -1),
                getPosition().shift(2, 1),
                getPosition().shift(1, 2),
                getPosition().shift(1, -2)
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
