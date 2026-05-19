package com.nargiz.chess.shared.models.figures;

import com.nargiz.chess.shared.command.ActionCommand;
import com.nargiz.chess.shared.models.*;
import com.nargiz.chess.shared.models.actions.KillFigure;
import com.nargiz.chess.shared.models.actions.MoveFigure;
import com.nargiz.chess.shared.models.actions.TransformFigure;
import com.nargiz.chess.shared.models.enums.FigureType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static com.nargiz.chess.shared.models.enums.FigureType.PAWN;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
public class PawnData extends FigureData {

    {
        setFigureType(PAWN);
    }

    public int getLastLine() {
        return switch (getColor()) {
            case WHITE -> 8;
            case BLACK -> 1;
        };
    }

    public int getDirection() {
        return switch (getColor()) {
            case WHITE -> 1;
            case BLACK -> -1;
        };
    }

    @Override
    public ActionResult validateAction(GameData gameData, ActionCommand action) {
        ActionResult result = new ActionResult();
        CellPosition targetPosition = action.getToPosition();
        if (isOutOfBoard(targetPosition)) {
            result.addError(new ValidationError("Move out of board"));
            return result;
        }

        if (isMoved() && isJump(targetPosition)
        ) {
            result.addError(new ValidationError("Pawn is already moved"));
            return result;
        }

        boolean isEnPassantAttack = isEnPassant(gameData, targetPosition);

        if (getPosition().equals(targetPosition) ||
                (
                        !isAttack(targetPosition) &&
                        !isEnPassantAttack &&
                        !isSingleMove(targetPosition) &&
                        !isJump(targetPosition)
                )
        ) {
            result.addError(new ValidationError("Impossible move"));
            return result;
        }

        FigureData attackFigure = null;
        for(FigureData figure: gameData.getFigures().values()) {
            if (figure.isDead() || figure == this) continue;

            if ( !figure.getPosition().equals(targetPosition) && figure.isOpponentColor(gameData.getCurrentPlayer().getKing()) &&
                    figure.isUnderAttack(gameData, gameData.getCurrentPlayer().getKing().getPosition(), this, targetPosition)
            ) {
                result.addError(new ValidationError("King is under attack"));
                return result;
            }

            if (
                    figure.getPosition().equals(targetPosition) &&
                    figure.getPosition().isSameColumn(getPosition())
            ) {
                result.addError(new ValidationError("Target cell is occupied"));
                return result;
            }

            if (
                    figure.getPosition().equals(targetPosition)
                            && !figure.getColor().equals(getColor())
                    && (isEnPassantAttack || isAttack(targetPosition))
            ) {
                attackFigure = figure;
            }

            if (
                    figure.getPosition().isSameColumn(getPosition()) &&
                    figure.getPosition().isRowBetween(targetPosition, getPosition())
            ) {
                result.addError(new ValidationError("Impossible move"));
                return result;
            }

        }

        if (attackFigure == null && !getPosition().isSameColumn(targetPosition)) {
            result.addError(new ValidationError("Impossible move"));
            return result;
        }

        if (attackFigure != null) {
            result.addAction(new KillFigure(attackFigure, isEnPassantAttack));
        }
        result.addAction(new MoveFigure(this, targetPosition));
        if (targetPosition.getRow() == getLastLine()) {
            result.addAction(new TransformFigure(this, action.getTransform()));
        }
        return result;
    }

    private boolean isEnPassant(GameData gameData, CellPosition targetPosition) {
        if (gameData.getHistory() == null || gameData.getHistory().isEmpty()) {
            return false;
        }
        HistoryData historyData = gameData.getHistory().getLast();
        return historyData != null
                && historyData.getTo().equals(targetPosition)
                && PAWN.equals(historyData.getFigure())
                && Math.abs(historyData.getTo().getRow() - historyData.getFrom().getRow()) == 2
                && getPosition().isSameRow(targetPosition)
                && Math.abs(targetPosition.getColumn() - getPosition().getColumn()) == 1;
    }

    private boolean isSingleMove(CellPosition targetPosition) {
        return targetPosition.isSameColumn(getPosition()) &&
                targetPosition.getRow() - getPosition().getRow() == getDirection();
    }

    private boolean isJump(CellPosition targetPosition) {
        return targetPosition.isSameColumn(getPosition()) &&
                targetPosition.getRow() - getPosition().getRow() == 2 * getDirection();
    }

    private boolean isAttack(CellPosition targetPosition) {
        return targetPosition.getRow() - getPosition().getRow() == getDirection()
                && Math.abs(targetPosition.getColumn() - getPosition().getColumn()) == 1;
    }

    @Override
    public boolean isUnderAttack(GameData gameData, CellPosition targetPosition, FigureData exclude, CellPosition replacedPosition) {
        return isAttack(targetPosition);
    }

    @Override
    public boolean isLocked(GameData gameData) {
        CellPosition forwardPos = getPosition().shift(getDirection(), 0);
        FigureData forwardFigure = gameData.getFigureAt(forwardPos);

        CellPosition leftVictimPos = getPosition().shift(getDirection(), -1);
        CellPosition rightVictimPos = getPosition().shift(getDirection(), 1);
        FigureData leftVictim = gameData.getFigureAt(leftVictimPos);
        FigureData rightVictim = gameData.getFigureAt(rightVictimPos);

        boolean enPassantExists = false;
        if (!gameData.getHistory().isEmpty()) {
            HistoryData historyData = gameData.getHistory().getLast();
            enPassantExists = historyData.getFigure().equals(PAWN)
                    && getPosition().isSameRow(historyData.getTo())
                    && Math.abs(getPosition().getColumn() - historyData.getTo().getColumn()) == 1
                    && Math.abs(historyData.getTo().getRow() - historyData.getFrom().getRow()) == 2;
        }

        return (forwardPos.isOutOfBoard() || forwardFigure != null)
                && !enPassantExists
                && (leftVictim == null || leftVictim.isSameColor(this))
                && (rightVictim == null || rightVictim.isSameColor(this));
    }

}
