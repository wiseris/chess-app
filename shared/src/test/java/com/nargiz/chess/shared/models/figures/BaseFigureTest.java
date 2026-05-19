package com.nargiz.chess.shared.models.figures;

import com.nargiz.chess.shared.models.*;
import com.nargiz.chess.shared.models.actions.KillFigure;
import com.nargiz.chess.shared.models.actions.MoveFigure;
import com.nargiz.chess.shared.models.actions.TransformFigure;
import com.nargiz.chess.shared.models.enums.ColorType;
import com.nargiz.chess.shared.models.enums.FigureType;
import org.junit.jupiter.api.Assertions;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nargiz.chess.shared.models.enums.ColorType.BLACK;
import static com.nargiz.chess.shared.models.enums.ColorType.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class BaseFigureTest {
    public void assertError(String message, ActionResult result) {
        for(ValidationError error: result.getErrors()) {
            if (error.getMessage().equals(message)) {
                return;
            }
        }
        Assertions.fail("Error not found in result: %s".formatted(message));
    }

    public void assertKill(FigureData figure, ActionPerform action) {
        assertInstanceOf(KillFigure.class, action);
        KillFigure killFigure = (KillFigure) action;
        assertEquals(figure, killFigure.getFigure());
    }

    public void assertMove(FigureData figure, CellPosition position, ActionPerform action) {
        assertInstanceOf(MoveFigure.class, action);
        MoveFigure moveFigure = (MoveFigure) action;
        assertEquals(figure, moveFigure.getFigure());
        assertEquals(position, moveFigure.getTargetPosition());
    }

    public void assertTransform(FigureData figure, FigureType to, ActionPerform action) {
        assertInstanceOf(TransformFigure.class, action);
        TransformFigure transformFigure = (TransformFigure) action;
        assertEquals(figure, transformFigure.getFigure());
        assertEquals(to, transformFigure.getFigureType());
    }

    public BoardBuilder prepareBoard() {
        return new TestBoardBuilder();
    }

    private static class TestBoardBuilder implements BoardBuilder, FigureBuilder {
        public FigureBuilder prev;
        ColorType color;
        FigureType figureType;
        CellPosition position;
        boolean moved;
        boolean right;

        public TestBoardBuilder() {
            prev = null;
        }

        private TestBoardBuilder(FigureBuilder prev, boolean right, ColorType color, FigureType figureType) {
            this.prev = prev;
            this.color = color;
            this.figureType = figureType;
            this.right = right;
        }

        @Override
        public FigureBuilder getPrevious() {
            return prev;
        }

        public ColorType getColor() {
            return color;
        }

        public CellPosition getPosition() {
            return position;
        }

        public FigureType getFigureType() {
            return figureType;
        }

        public boolean isMoved() {
            return moved;
        }

        public boolean isRight() {
            return right;
        }

        public FigureBuilder with(ColorType color, FigureType figureType) {
            return new TestBoardBuilder(this, true, color, figureType);
        }

        public FigureBuilder withRight(ColorType color, FigureType figureType) {
            return new TestBoardBuilder(this, true, color, figureType);
        }

        public FigureBuilder withLeft(ColorType color, FigureType figureType) {
            return new TestBoardBuilder(this, false, color, figureType);
        }

        @Override
        public FigureBuilder at(int row, int column) {
            this.position = new CellPosition(row, column);
            return this;
        }

        @Override
        public FigureBuilder at(CellPosition position) {
            this.position = position;
            return this;
        }

        @Override
        public FigureBuilder moved() {
            this.moved = true;
            return this;
        }

        public GameData build() {
            MemberData whiteMember = new MemberData();
            MemberData blackMember = new MemberData();
            Map<ColorType, Map<Boolean, RookData>> rookMap =
                    Map.of(WHITE, new HashMap<>(),
                           BLACK, new HashMap<>());
            Map<CellPosition, FigureData> figures = new HashMap<>();
            FigureBuilder builder = this;
            while (builder.getPrevious() != null) {
                FigureData figureData = switch (builder.getFigureType()) {
                    case PAWN -> new PawnData();
                    case ROOK -> new RookData();
                    case KNIGHT -> new KnightData();
                    case BISHOP -> new BishopData();
                    case QUEEN -> new QueenData();
                    case KING -> new KingData();
                };
                figureData.setPosition(builder.getPosition());
                figureData.setMoved(builder.isMoved());
                figureData.setColor(builder.getColor());
                if (figureData instanceof RookData rookData) {
                    rookMap.get(builder.getColor())
                            .put(builder.isRight(), rookData);
                }
                if (figureData instanceof KingData kingData) {
                    if (builder.getColor() == WHITE) {
                        whiteMember.setKing(kingData);
                    } else {
                        blackMember.setKing(kingData);
                    }
                }
                figures.put(figureData.getPosition(), figureData);

                builder = builder.getPrevious();
            }
            if (whiteMember.getKing() != null) {
                whiteMember.getKing().rightRook = rookMap.get(WHITE).get(true);
                whiteMember.getKing().leftRook = rookMap.get(WHITE).get(false);
            }
            if (blackMember.getKing() != null) {
                blackMember.getKing().rightRook = rookMap.get(BLACK).get(true);
                blackMember.getKing().leftRook = rookMap.get(BLACK).get(false);
            }

            return GameData.builder()
                    .figures(figures)
                    .white(whiteMember)
                    .black(blackMember)
                    .history(new ArrayList<>())
                    .build();
        }
    }

    public interface BoardBuilder {
        FigureBuilder with(ColorType color, FigureType figureType);
        FigureBuilder withRight(ColorType color, FigureType figureType);
        FigureBuilder withLeft(ColorType color, FigureType figureType);
    }

    public interface FigureBuilder {
        FigureBuilder getPrevious();
        ColorType getColor();
        CellPosition getPosition();
        FigureType getFigureType();
        boolean isMoved();
        boolean isRight();

        FigureBuilder with(ColorType color, FigureType figureType);
        FigureBuilder withRight(ColorType color, FigureType figureType);
        FigureBuilder withLeft(ColorType color, FigureType figureType);
        FigureBuilder at(int row, int column);
        FigureBuilder at(CellPosition position);
        FigureBuilder moved();
        GameData build();
    }
}
