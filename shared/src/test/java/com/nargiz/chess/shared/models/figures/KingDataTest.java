package com.nargiz.chess.shared.models.figures;

import com.nargiz.chess.shared.command.ActionCommand;
import com.nargiz.chess.shared.models.ActionResult;
import com.nargiz.chess.shared.models.CellPosition;
import com.nargiz.chess.shared.models.GameData;
import org.junit.jupiter.api.*;

import static com.nargiz.chess.shared.models.enums.ColorType.BLACK;
import static com.nargiz.chess.shared.models.enums.ColorType.WHITE;
import static com.nargiz.chess.shared.models.enums.FigureType.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class KingDataTest extends BaseFigureTest  {

    @Nested
    @Order(1)
    @DisplayName("King ValidateAction tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class KingValidateActionTest {
        @Test
        @Order(1)
        @DisplayName("King movement forward test")
        void moveSuccessForwardTest() {
            CellPosition testKingPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKingPosition.shift(1, 0))
                    .build();

            ActionResult result = kingData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(kingData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(2)
        @DisplayName("King movement diagonal test")
        void moveSuccessDiagonalTest() {
            CellPosition testKingPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKingPosition.shift(1, 1))
                    .build();

            ActionResult result = kingData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(kingData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(3)
        @DisplayName("King movement under attack test")
        void moveFailUnderAttackTest() {
            CellPosition testKingPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(testKingPosition.shift(3, 3))
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKingPosition.shift(0, 1))
                    .build();

            ActionResult result = kingData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(kingData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(4)
        @DisplayName("King movement to attacked cell")
        void moveFailMoveToAttackedCellTest() {
            CellPosition testKingPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(testKingPosition.shift(-2, 3))
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKingPosition.shift(1, 0))
                    .build();

            ActionResult result = kingData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Target cell is under attack", result);
        }

        @Test
        @Order(5)
        @DisplayName("King castling right Success test")
        void castlingRightSuccessTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testRookPosition = new CellPosition(1, 8);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);
            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKingPosition.shift(0, 2))
                    .build();

            ActionResult result = kingData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(kingData, actionCommand.getToPosition(), result.getUpdates().get(0));
            assertMove(rookData, actionCommand.getToPosition().shift(0, -1), result.getUpdates().get(1));
        }

        @Test
        @Order(6)
        @DisplayName("King castling left Success test")
        void castlingLeftSuccessTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testRookPosition = new CellPosition(1, 1);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .withLeft(WHITE, ROOK).at(testRookPosition)
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);
            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKingPosition.shift(0, -2))
                    .build();

            ActionResult result = kingData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(kingData, actionCommand.getToPosition(), result.getUpdates().get(0));
            assertMove(rookData, actionCommand.getToPosition().shift(0, 1), result.getUpdates().get(1));
        }

        @Test
        @Order(7)
        @DisplayName("King castling when King is under attack test")
        void castlingWhenKingIsUnderAttackTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testBishopPosition = testKingPosition.shift(3, 3);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(testBishopPosition)
                    .withLeft(WHITE, ROOK).at(new CellPosition(1, 1))
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKingPosition.shift(0, -2))
                    .build();

            ActionResult result = kingData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Castling is failed. King is under attack", result);
        }

        @Test
        @Order(8)
        @DisplayName("King castling when King way is under attack test")
        void castlingWhenKingWayIsUnderAttackTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testBishopPosition = testKingPosition.shift(3, 2);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(testBishopPosition)
                    .withLeft(WHITE, ROOK).at(new CellPosition(1, 1))
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKingPosition.shift(0, -2))
                    .build();

            ActionResult result = kingData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Castling is failed. Cell between is under attack", result);
        }

        @Test
        @Order(9)
        @DisplayName("King castling when target is under attack test")
        void castlingWhenTargetIsUnderAttackTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testBishopPosition = testKingPosition.shift(3, 1);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(testBishopPosition)
                    .withLeft(WHITE, ROOK).at(new CellPosition(1, 1))
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKingPosition.shift(0, -2))
                    .build();

            ActionResult result = kingData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Castling is failed. Target cell is under attack", result);
        }

        @Test
        @Order(10)
        @DisplayName("King castling King is Already test")
        void castlingFailKingIsMovedTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testRookPosition = new CellPosition(1, 8);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition).moved()
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);
            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKingPosition.shift(0, 2))
                    .build();

            ActionResult result = kingData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Castling is failed. King is already moved", result);
        }

        @Test
        @Order(11)
        @DisplayName("King castling Rook is Already test")
        void castlingFailRookIsMovedTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testRookPosition = new CellPosition(1, 8);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .withRight(WHITE, ROOK).at(testRookPosition).moved()
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);
            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKingPosition.shift(0, 2))
                    .build();

            ActionResult result = kingData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Castling is failed. Right rook is already moved", result);
        }

        @Test
        @Order(12)
        @DisplayName("King castling Rook is dead test")
        void castlingFailRookIsDeadTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testRookPosition = new CellPosition(1, 8);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);
            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);
            rookData.setDead(true);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKingPosition.shift(0, 2))
                    .build();

            ActionResult result = kingData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Castling is failed. Right rook is dead", result);
        }

        @Test
        @Order(13)
        @DisplayName("King castling different row")
        void castlingFailDifferentRowTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testRookPosition = new CellPosition(1, 8);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);
            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKingPosition.shift(1, 2))
                    .build();

            ActionResult result = kingData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Impossible move", result);
        }

    }

    @Nested
    @Order(2)
    @DisplayName("King isUnderAttack tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class KingIsUnderAttackTest {
        @Test
        @Order(1)
        @DisplayName("Check attacking Left cells")
        void isUnderAttackLeftTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = kingData.isUnderAttack(gameData, testKingPosition.shift(0, -1), kingData, kingData.getPosition());

            assertTrue(isAttacked);
        }

        @Test
        @Order(2)
        @DisplayName("Check attacking right cells")
        void isUnderAttackRightTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = kingData.isUnderAttack(gameData, testKingPosition.shift(0, 1), kingData, kingData.getPosition());

            assertTrue(isAttacked);
        }

        @Test
        @Order(3)
        @DisplayName("Check attacking Right Up cells")
        void isUnderAttackRightUpTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = kingData.isUnderAttack(gameData, testKingPosition.shift(1, 1), kingData, kingData.getPosition());

            assertTrue(isAttacked);
        }

        @Test
        @Order(4)
        @DisplayName("Check attacking impossible cells")
        void isUnderAttackImpossibleTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .build();

            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = kingData.isUnderAttack(gameData, testKingPosition.shift(1, 2), kingData, kingData.getPosition());

            assertFalse(isAttacked);
        }

    }

}
