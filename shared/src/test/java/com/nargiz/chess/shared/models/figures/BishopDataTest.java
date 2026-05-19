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
public class BishopDataTest extends BaseFigureTest {

    @Nested
    @Order(1)
    @DisplayName("Bishop ValidateAction tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class BishopValidateActionTest {
        @Test
        @Order(1)
        @DisplayName("Bishop movement diagonal left down right test")
        void moveSuccessDiagonalLeftDownTest() {
            CellPosition testBishopPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .with(WHITE, BISHOP).at(testBishopPosition)
                    .build();

            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .fromPosition(testBishopPosition)
                    .toPosition(testBishopPosition.shift(-3, -3))
                    .build();

            ActionResult result = bishopData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(bishopData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(2)
        @DisplayName("Bishop movement right down test")
        void moveSuccessRightDownTest() {
            CellPosition testBishopPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .with(WHITE, BISHOP).at(testBishopPosition)
                    .build();

            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .fromPosition(testBishopPosition)
                    .toPosition(testBishopPosition.shift(3, -3))
                    .build();

            ActionResult result = bishopData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(bishopData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(3)
        @DisplayName("Bishop movement out of board")
        void moveOutOfBoardTest() {
            CellPosition testBishopPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .with(WHITE, BISHOP).at(testBishopPosition)
                    .build();

            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testBishopPosition.shift(-8, -8))
                    .build();

            ActionResult result = bishopData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Move out of board", result);
        }

        @Test
        @Order(4)
        @DisplayName("Bishop impossible move")
        void moveImpossibleTest() {
            CellPosition testBishopPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,1)
                    .with(WHITE, BISHOP).at(testBishopPosition)
                    .build();

            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testBishopPosition.shift(1, 2))
                    .build();

            ActionResult result = bishopData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Impossible move", result);
        }

        @Test
        @Order(5)
        @DisplayName("King on the road")
        void moveKingOnTheRoadTest() {
            CellPosition testBishopPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testBishopPosition.shift(-3, -3))
                    .with(WHITE, BISHOP).at(testBishopPosition)
                    .build();

            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testBishopPosition.shift(-4, -4))
                    .build();

            ActionResult result = bishopData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Impossible move", result);
        }

        @Test
        @Order(6)
        @DisplayName("Enemy attacks King, but Bishop is bit him")
        void moveEnemyAttackTest() {
            CellPosition targetPosition = new CellPosition(1, 5);
            CellPosition testBishopPosition = targetPosition.shift(3 , 3);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(targetPosition.shift(4, -4))
                    .with(BLACK, BISHOP).at(targetPosition)
                    .with(WHITE, BISHOP).at(testBishopPosition)
                    .build();

            BishopData testBishopData = (BishopData) gameData.getFigureAt(testBishopPosition);
            BishopData bishopData = (BishopData) gameData.getFigureAt(targetPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(targetPosition)
                    .build();

            ActionResult result = testBishopData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertKill(bishopData, result.getUpdates().get(0));
            assertMove(testBishopData, targetPosition, result.getUpdates().get(1));
        }

        @Test
        @Order(7)
        @DisplayName("King under attack")
        void moveKingUnderAttackTest() {
            CellPosition enemyBishopPosition = new CellPosition(1, 5);
            CellPosition testBishopPosition = enemyBishopPosition.shift(1, -1);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(enemyBishopPosition.shift(4, -4))
                    .with(BLACK, BISHOP).at(enemyBishopPosition)
                    .withRight(WHITE, BISHOP).at(testBishopPosition)
                    .build();

            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testBishopPosition.shift(1, 1))
                    .build();

            ActionResult result = bishopData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("King is under attack", result);
        }
    }

    @Nested
    @Order(2)
    @DisplayName("Bishop isUnderAttack tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class BishopIsUnderAttackTest {
        @Test
        @Order(1)
        @DisplayName("Check attacking cells")
        void isUnderAttackHorizontalTest() {
            CellPosition testBishopPosition = new CellPosition(5, 6);
            CellPosition testKingPosition = testBishopPosition.shift(-1, -2);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(5, 2)
                    .withRight(WHITE, BISHOP).at(testBishopPosition)
                    .build();

            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = bishopData.isUnderAttack(gameData, new CellPosition(7, 8), kingData, kingData.getPosition().shift(1, 0));

            assertTrue(isAttacked);
        }

        @Test
        @Order(2)
        @DisplayName("The king is blocking")
        void theKingIsBlockingTest() {
            CellPosition testBishopPosition = new CellPosition(5, 5);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(5, 2)
                    .withRight(WHITE, BISHOP).at(testBishopPosition)
                    .build();

            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = bishopData.isUnderAttack(gameData, new CellPosition(2, 2), kingData, new CellPosition(4, 4));

            assertFalse(isAttacked);
        }

        @Test
        @Order(3)
        @DisplayName("The king is not blocking")
        void theKingIsNotBlockingTest() {
            CellPosition testBishopPosition = new CellPosition(5, 5);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(5, 2)
                    .withRight(WHITE, BISHOP).at(testBishopPosition)
                    .build();

            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = bishopData.isUnderAttack(gameData, new CellPosition(3, 3), kingData, kingData.getPosition().shift(-1 , 0));

            assertTrue(isAttacked);
        }

        @Test
        @Order(4)
        @DisplayName("The king is my target")
        void theKingIsMyTargetTest() {
            CellPosition testBishopPosition = new CellPosition(5, 5);
            CellPosition testKingPosition = new CellPosition(2, 2);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(5, 2)
                    .withRight(WHITE, BISHOP).at(testBishopPosition)
                    .build();

            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = bishopData.isUnderAttack(gameData, new CellPosition(2, 2), kingData, new CellPosition(5, 4));

            assertTrue(isAttacked);
        }

        @Test
        @Order(5)
        @DisplayName("The king is not blocking, but the Bishop is")
        void theKingIsNotBlockingButBishopIsTest() {
            CellPosition testBishopPosition = new CellPosition(5, 5);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(3, 3)
                    .withRight(WHITE, BISHOP).at(testBishopPosition)
                    .build();

            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = bishopData.isUnderAttack(gameData, new CellPosition(1, 1), kingData, kingData.getPosition().shift(1, 0));

            assertFalse(isAttacked);
        }

        @Test
        @Order(6)
        @DisplayName("I cannot even move there")
        void iCannotEvenMoveThereTest() {
            CellPosition testBishopPosition = new CellPosition(5, 5);
            CellPosition testKingPosition = new CellPosition(4, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(BLACK, BISHOP).at(5, 2)
                    .withRight(WHITE, BISHOP).at(testBishopPosition)
                    .build();

            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = bishopData.isUnderAttack(gameData, new CellPosition(2, 3), kingData, kingData.getPosition());

            assertFalse(isAttacked);
        }
    }

}
