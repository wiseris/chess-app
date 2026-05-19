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
@AllArgsConstructor
@ToString
public class KingData extends FigureData {
    RookData leftRook;
    RookData rightRook;

    {
        setFigureType(FigureType.KING);
    }

    @Override
    public ActionResult validateAction(GameData gameData, ActionCommand action) {
        ActionResult result = new ActionResult();
        CellPosition targetPosition = action.getToPosition();
        if (isOutOfBoard(targetPosition)) {
            result.addError(new ValidationError("Move out of board"));
            return result;
        }

        if (Math.abs(targetPosition.getColumn() - getPosition().getColumn()) == 2) {
            return tryCastling(gameData, targetPosition);
        }

        if (getPosition().equals(targetPosition) ||
                Math.abs(targetPosition.getColumn() - getPosition().getColumn()) > 1 ||
                Math.abs(targetPosition.getRow() - getPosition().getRow()) > 1
        ) {
            result.addError(new ValidationError("Impossible move"));
            return result;
        }

        FigureData attackFigure = null;
        for(FigureData figure: gameData.getFigures().values()) {
            if (figure.isDead() || figure == this) continue;
            if (
                    figure.getPosition().equals(targetPosition)
                 && figure.getColor().equals(getColor())
            ) {
                result.addError(new ValidationError("Target cell is occupied by ally"));
                return result;
            }

            if (
                    !figure.getColor().equals(getColor())
                    && figure.isUnderAttack(gameData, targetPosition, this, targetPosition)
            ) {
                result.addError(new ValidationError("Target cell is under attack"));
                return result;
            }

            if (
                    figure.getPosition().equals(targetPosition)
                 && !figure.getColor().equals(getColor())
            ) {
                attackFigure = figure;
            }
        }

        if (attackFigure != null) {
            result.addAction(new KillFigure(attackFigure));
        }
        result.addAction(new MoveFigure(this, targetPosition));
        return result;
    }

    @Override
    public boolean isUnderAttack(GameData gameData, CellPosition targetPosition, FigureData exclude, CellPosition replacedPosition) {
        return !getPosition().equals(targetPosition)
              && Math.abs(targetPosition.getColumn() - getPosition().getColumn()) < 2
              && Math.abs(targetPosition.getRow() - getPosition().getRow()) < 2;
    }

    private ActionResult tryCastling(GameData gameData, CellPosition targetPosition) {
        ActionResult result = new ActionResult();
        if (isMoved()) {
            result.addError(new ValidationError("Castling is failed. King is already moved"));
            return result;
        }
        if (targetPosition.getRow() != getPosition().getRow()) {
            result.addError(new ValidationError("Impossible move"));
            return result;
        }
        RookData rookData = rightRook;
        int shift = 1;
        if (targetPosition.getColumn() - getPosition().getColumn() == -2) {
            if (leftRook.isDead()) {
                result.addError(new ValidationError("Castling is failed. Left rook is dead"));
                return result;
            }

            if (leftRook.isMoved()) {
                result.addError(new ValidationError("Castling is failed. Left rook is already moved"));
                return result;
            }

            rookData = leftRook;
            shift = -1;
        }

        if (targetPosition.getColumn() - getPosition().getColumn() == 2) {
            if (rightRook.isDead()) {
                result.addError(new ValidationError("Castling is failed. Right rook is dead"));
                return result;
            }

            if (rightRook.isMoved()) {
                result.addError(new ValidationError("Castling is failed. Right rook is already moved"));
                return result;
            }

            rookData = rightRook;
            shift = 1;
        }

        for(FigureData figure: gameData.getFigures().values()) {
            if (figure.isDead() || figure == this) continue;
            if (
                   figure.getPosition().getRow() == getPosition().getRow()
                && figure.getPosition().isColumnBetween(rookData.getPosition(), getPosition())
            ) {
                result.addError(new ValidationError("Castling is failed. Cell between is occupied"));
                return result;
            }

            if (
                    !figure.getColor().equals(getColor()) &&
                    figure.isUnderAttack(gameData, getPosition(), this, getPosition())
            ) {
                result.addError(new ValidationError("Castling is failed. King is under attack"));
                return result;
            }

            if (
                    !figure.getColor().equals(getColor()) &&
                    figure.isUnderAttack(gameData, targetPosition, this, targetPosition.shift(0, -shift))
            ) {
                result.addError(new ValidationError("Castling is failed. Target cell is under attack"));
                return result;
            }

            if (
                    !figure.getColor().equals(getColor()) &&
                    figure.isUnderAttack(gameData, targetPosition.shift(0, -shift), this, targetPosition.shift(0, -shift))
            ) {
                result.addError(new ValidationError("Castling is failed. Cell between is under attack"));
                return result;
            }
        }
        result.addAction(new MoveFigure(this, targetPosition));
        result.addAction(new MoveFigure(rookData, targetPosition.shift(0, -shift)));
        return result;
    }

    @Override
    public boolean isLocked(GameData gameData) {
        for (int row = -1; row <= 1; row++) {
            for (int column = -1; column <= 1; column++) {
                if (row == 0 && column == 0) continue;
                CellPosition vPos = getPosition().shift(row, column);
                if (vPos.isOutOfBoard()) continue;
                FigureData figure = gameData.getFigureAt(vPos);
                if (figure != null && figure.isAlive() && figure.isSameColor(this)) continue;

                boolean isUnderAttack = gameData.getFigures().values().stream()
                        .filter(FigureData::isAlive)
                        .filter(f -> f.isOpponentColor(this))
                        .anyMatch(f -> f.isUnderAttack(gameData, vPos, f, vPos));
                if (!isUnderAttack) {
                    return false;
                }
            }
        }
        return true;
    }
}
