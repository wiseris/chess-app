package com.nargiz.chess.shared.models.figures;

import com.nargiz.chess.shared.command.ActionCommand;
import com.nargiz.chess.shared.models.ActionResult;
import com.nargiz.chess.shared.models.CellPosition;
import com.nargiz.chess.shared.models.GameData;
import org.junit.jupiter.api.*;

import static com.nargiz.chess.shared.models.enums.ColorType.*;
import static com.nargiz.chess.shared.models.enums.FigureType.*;
import static org.junit.jupiter.api.Assertions.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class RookDataTest extends BaseFigureTest {

    @Nested
    @Order(1)
    @DisplayName("Rook ValidateAction tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class RookValidateActionTest {
        @Test
        @Order(1)
        @DisplayName("Rook movement vertical test")
        void moveSuccessVerticalTest() {
            CellPosition testRookPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .fromPosition(testRookPosition)
                    .toPosition(testRookPosition.shift(-1, 0))
                    .build();

            ActionResult result = rookData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(rookData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(2)
        @DisplayName("Rook movement horizontal test")
        void moveSuccessHorizontalTest() {
            CellPosition testRookPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testRookPosition.shift(0, -4))
                    .build();

            ActionResult result = rookData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(rookData, actionCommand.getToPosition(), result.getUpdates().getFirst());

        }

        @Test
        @Order(3)
        @DisplayName("Rook movement out of board")
        void moveOutOfBoardTest() {
            CellPosition testRookPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testRookPosition.shift(0, -8))
                    .build();

            ActionResult result = rookData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Move out of board", result);
        }

        @Test
        @Order(4)
        @DisplayName("Rook impossible move")
        void moveImpossibleTest() {
            CellPosition testRookPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testRookPosition.shift(1, 2))
                    .build();

            ActionResult result = rookData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Impossible move", result);
        }

        @Test
        @Order(5)
        @DisplayName("King on the road")
        void moveKingOnTheRoadTest() {
            CellPosition testRookPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testRookPosition.shift(0, -3))
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testRookPosition.shift(0, -4))
                    .build();

            ActionResult result = rookData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Impossible move", result);
        }

        @Test
        @Order(6)
        @DisplayName("Enemy attacks King, but Rook is bit him")
        void moveEnemyAttackTest() {
            CellPosition testRookPosition = new CellPosition(5, 5);
            CellPosition targetPosition = new CellPosition(1, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(targetPosition.shift(4, -4))
                    .with(BLACK, BISHOP).at(targetPosition)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);
            BishopData bishopData = (BishopData) gameData.getFigureAt(targetPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(targetPosition)
                    .build();

            ActionResult result = rookData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertKill(bishopData, result.getUpdates().get(0));
            assertMove(rookData, targetPosition, result.getUpdates().get(1));
        }

        @Test
        @Order(7)
        @DisplayName("King under attack")
        void moveKingUnderAttackTest() {
            CellPosition enemyBishopPosition = new CellPosition(1, 5);
            CellPosition testRookPosition = enemyBishopPosition.shift(1, -1);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(enemyBishopPosition.shift(4, -4))
                    .with(BLACK, BISHOP).at(enemyBishopPosition)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testRookPosition.shift(1, 0))
                    .build();

            ActionResult result = rookData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("King is under attack", result);
        }
    }

    @Nested
    @Order(2)
    @DisplayName("Rook isUnderAttack tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class RookIsUnderAttackTest {
        @Test
        @Order(1)
        @DisplayName("Check attacking cells")
        void isUnderAttackHorizontalTest() {
            CellPosition testRookPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = testRookPosition.shift(-1, -2);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(5, 2)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = rookData.isUnderAttack(gameData, new CellPosition(5, 8), kingData, kingData.getPosition().shift(1, 0));

            assertTrue(isAttacked);
        }

        @Test
        @Order(2)
        @DisplayName("The king is blocking")
        void theKingIsBlockingTest() {
            CellPosition testRookPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(5, 2)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = rookData.isUnderAttack(gameData, new CellPosition(5, 3), kingData, new CellPosition(5, 4));

            assertFalse(isAttacked);
        }

        @Test
        @Order(3)
        @DisplayName("The king is not blocking")
        void theKingIsNotBlockingTest() {
            CellPosition testRookPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = new CellPosition(5, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(5, 2)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = rookData.isUnderAttack(gameData, new CellPosition(5, 3), kingData, kingData.getPosition().shift(-1 , 0));

            assertTrue(isAttacked);
        }

        @Test
        @Order(4)
        @DisplayName("The king is my target")
        void theKingIsMyTargetTest() {
            CellPosition testRookPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(5, 2)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = rookData.isUnderAttack(gameData, new CellPosition(5, 4), kingData, new CellPosition(5, 4));

            assertTrue(isAttacked);
        }

        @Test
        @Order(5)
        @DisplayName("The king is not blocking, but the Bishop is")
        void theKingIsNotBlockingButBishopIsTest() {
            CellPosition testRookPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(5, 2)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = rookData.isUnderAttack(gameData, new CellPosition(5, 1), kingData, kingData.getPosition());

            assertFalse(isAttacked);
        }

        @Test
        @Order(6)
        @DisplayName("I cannot even move there")
        void iCannotEvenMoveThereTest() {
            CellPosition testRookPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(5, 2)
                    .withRight(WHITE, ROOK).at(testRookPosition)
                    .build();

            RookData rookData = (RookData) gameData.getFigureAt(testRookPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = rookData.isUnderAttack(gameData, new CellPosition(1, 3), kingData, kingData.getPosition());

            assertFalse(isAttacked);
        }
    }

}
