package com.nargiz.chess.shared.models.figures;

import com.nargiz.chess.shared.command.ActionCommand;
import com.nargiz.chess.shared.models.*;
import com.nargiz.chess.shared.models.actions.KillFigure;
import com.nargiz.chess.shared.models.actions.MoveFigure;
import com.nargiz.chess.shared.models.enums.ColorType;
import org.junit.jupiter.api.*;

import java.util.List;

import static com.nargiz.chess.shared.models.enums.ColorType.BLACK;
import static com.nargiz.chess.shared.models.enums.ColorType.WHITE;
import static com.nargiz.chess.shared.models.enums.FigureType.*;
import static org.junit.jupiter.api.Assertions.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class QueenDataTest extends BaseFigureTest {

    @Nested
    @Order(1)
    @DisplayName("Queen ValidateAction tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class QueenValidateActionTest {
        @Test
        @Order(1)
        @DisplayName("Queen movement vertical test")
        void moveSuccessVerticalTest() {
            CellPosition testQueenPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .withRight(WHITE, QUEEN).at(testQueenPosition)
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testQueenPosition.shift(-1, 0))
                    .build();

            ActionResult result = queenData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(queenData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(2)
        @DisplayName("Queen movement horizontal test")
        void moveSuccessHorizontalTest() {
            CellPosition testQueenPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .withRight(WHITE, QUEEN).at(testQueenPosition)
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testQueenPosition.shift(0, -3))
                    .build();

            ActionResult result = queenData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(queenData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(3)
        @DisplayName("Queen diagonal up-right test")
        void moveSuccessDiagonalUpRightTest() {
            CellPosition testQueenPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .withRight(WHITE, QUEEN).at(testQueenPosition)
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testQueenPosition.shift(-2, 2))
                    .build();

            ActionResult result = queenData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(queenData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(4)
        @DisplayName("Queen diagonal down-right test")
        void moveSuccessDiagonalDownRightTest() {
            CellPosition testQueenPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .withRight(WHITE, QUEEN).at(testQueenPosition)
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testQueenPosition.shift(2, 2))
                    .build();

            ActionResult result = queenData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(queenData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(5)
        @DisplayName("Queen movement out of board")
        void moveOutOfBoardTest() {
            CellPosition testQueenPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .withRight(WHITE, QUEEN).at(testQueenPosition)
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testQueenPosition.shift(-6, -6))
                    .build();

            ActionResult result = queenData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Move out of board", result);
        }

        @Test
        @Order(6)
        @DisplayName("Queen impossible move")
        void moveImpossibleTest() {
            CellPosition testQueenPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .withRight(WHITE, QUEEN).at(testQueenPosition)
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testQueenPosition.shift(3, 2))
                    .build();

            ActionResult result = queenData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Impossible move", result);
        }

        @Test
        @Order(7)
        @DisplayName("King on the road")
        void moveKingOnTheRoadTest() {
            CellPosition testQueenPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(5,2)
                    .withRight(WHITE, QUEEN).at(testQueenPosition)
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(new CellPosition(5, 1))
                    .build();

            ActionResult result = queenData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            ValidationError error = result.getErrors().getFirst();
            assertEquals("Impossible move", error.getMessage());
        }

        @Test
        @Order(8)
        @DisplayName("Enemy attack")
        void moveEnemyAttackTest() {
            CellPosition testQueenPosition = new CellPosition(5, 5);
            CellPosition testBishopPosition = new CellPosition(5, 8);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(5,2)
                    .with(WHITE, QUEEN).at(testQueenPosition)
                    .with(BLACK, BISHOP).at(testBishopPosition)
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);
            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testBishopPosition)
                    .build();

            ActionResult result = queenData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertKill(bishopData, result.getUpdates().get(0));
            assertMove(queenData, actionCommand.getToPosition(), result.getUpdates().get(1));

        }

        @Test
        @Order(9)
        @DisplayName("King under attack")
        void moveKingUnderAttackTest() {
            CellPosition testQueenPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .with(WHITE, QUEEN).at(testQueenPosition)
                    .with(BLACK, BISHOP).at(new CellPosition(8, 8))
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(new CellPosition(5, 1))
                    .build();

            ActionResult result = queenData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("King is under attack", result);
        }
    }

    @Nested
    @Order(2)
    @DisplayName("Queen isUnderAttack tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class QueenIsUnderAttackTest {
        @Test
        @Order(1)
        @DisplayName("Check attacking cells")
        void isUnderAttackHorizontalTest() {
            CellPosition testQueenPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, QUEEN).at(testQueenPosition)
                    .with(BLACK, BISHOP).at(new CellPosition(5, 2))
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = queenData.isUnderAttack(gameData, testQueenPosition.shift(0, 2), kingData, new CellPosition(5, 4));

            assertTrue(isAttacked);
        }

        @Test
        @Order(2)
        @DisplayName("Check attacking cells diagonal")
        void isUnderAttackDiagonalTest() {
            CellPosition testQueenPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, QUEEN).at(testQueenPosition)
                    .with(BLACK, BISHOP).at(new CellPosition(5, 2))
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = queenData.isUnderAttack(gameData, testQueenPosition.shift(2, -2), kingData, new CellPosition(5, 4));

            assertTrue(isAttacked);
        }

        @Test
        @Order(3)
        @DisplayName("The king is blocking")
        void theKingIsBlockingTest() {
            CellPosition testQueenPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, QUEEN).at(testQueenPosition)
                    .with(BLACK, BISHOP).at(new CellPosition(5, 2))
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = queenData.isUnderAttack(gameData, new CellPosition(5, 3), kingData, new CellPosition(5, 4));

            assertFalse(isAttacked);
        }

        @Test
        @Order(4)
        @DisplayName("The king is not blocking")
        void theKingIsNotBlockingTest() {
            CellPosition testQueenPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, QUEEN).at(testQueenPosition)
                    .with(BLACK, BISHOP).at(new CellPosition(5, 2))
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = queenData.isUnderAttack(gameData, new CellPosition(5, 3), kingData, kingData.getPosition());

            assertTrue(isAttacked);
        }

        @Test
        @Order(5)
        @DisplayName("The king is my target")
        void theKingIsMyTargetTest() {
            CellPosition testQueenPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, QUEEN).at(testQueenPosition)
                    .with(BLACK, BISHOP).at(new CellPosition(5, 2))
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = queenData.isUnderAttack(gameData, new CellPosition(5, 4), kingData, new CellPosition(5, 4));

            assertTrue(isAttacked);
        }

        @Test
        @Order(6)
        @DisplayName("The king is not blocking, but the Bishop is")
        void theKingIsNotBlockingButBishopIsTest() {
            CellPosition testQueenPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, QUEEN).at(testQueenPosition)
                    .with(BLACK, BISHOP).at(new CellPosition(5, 2))
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = queenData.isUnderAttack(gameData, new CellPosition(5, 1), kingData, kingData.getPosition());

            assertFalse(isAttacked);
        }

        @Test
        @Order(7)
        @DisplayName("I cannot even move there")
        void iCannotEvenMoveThereTest() {
            CellPosition testQueenPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, QUEEN).at(testQueenPosition)
                    .with(BLACK, BISHOP).at(new CellPosition(5, 2))
                    .build();

            QueenData queenData = (QueenData) gameData.getFigureAt(testQueenPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = queenData.isUnderAttack(gameData, new CellPosition(1, 3), kingData, kingData.getPosition());

            assertFalse(isAttacked);
        }
    }

}
