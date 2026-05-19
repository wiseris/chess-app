package com.nargiz.chess.shared.models.figures;

import com.nargiz.chess.shared.command.ActionCommand;
import com.nargiz.chess.shared.models.ActionResult;
import com.nargiz.chess.shared.models.CellPosition;
import com.nargiz.chess.shared.models.GameData;
import com.nargiz.chess.shared.models.enums.FigureType;
import org.junit.jupiter.api.*;

import static com.nargiz.chess.shared.models.enums.ColorType.BLACK;
import static com.nargiz.chess.shared.models.enums.ColorType.WHITE;
import static com.nargiz.chess.shared.models.enums.FigureType.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class KnightDataTest extends BaseFigureTest  {

    @Nested
    @Order(1)
    @DisplayName("Knight ValidateAction tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class KnightValidateActionTest {
        @Test
        @Order(1)
        @DisplayName("Knight movement 2r + 1c test")
        void moveSuccessTwoPlusOneTest() {
            CellPosition testKnightPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,5)
                    .with(WHITE, KNIGHT).at(testKnightPosition)
                    .build();

            KnightData knightData = (KnightData) gameData.getFigureAt(testKnightPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKnightPosition.shift(2, 1))
                    .build();

            ActionResult result = knightData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(knightData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(2)
        @DisplayName("Knight movement 1r + 2c test")
        void moveSuccessOnePlusTwoTest() {
            CellPosition testKnightPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,5)
                    .with(WHITE, KNIGHT).at(testKnightPosition)
                    .build();

            KnightData knightData = (KnightData) gameData.getFigureAt(testKnightPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKnightPosition.shift(1, 2))
                    .build();

            ActionResult result = knightData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(knightData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(3)
        @DisplayName("Knight movement Surrounded But Free test")
        void moveSuccessSurroundedButFreeTest() {
            CellPosition testKnightPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,5)
                    .with(WHITE, KNIGHT).at(testKnightPosition)
                    .with(WHITE, PAWN).at(testKnightPosition.shift(-1, 1))
                    .with(WHITE, PAWN).at(testKnightPosition.shift(0, 1))
                    .with(WHITE, PAWN).at(testKnightPosition.shift(1, 1))
                    .with(WHITE, PAWN).at(testKnightPosition.shift(-1, -1))
                    .with(WHITE, PAWN).at(testKnightPosition.shift(0, -1))
                    .with(WHITE, PAWN).at(testKnightPosition.shift(1, -1))
                    .with(WHITE, PAWN).at(testKnightPosition.shift(-1, 0))
                    .with(WHITE, PAWN).at(testKnightPosition.shift(1, 0))
                    .build();

            KnightData knightData = (KnightData) gameData.getFigureAt(testKnightPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKnightPosition.shift(1, 2))
                    .build();

            ActionResult result = knightData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(knightData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(4)
        @DisplayName("Knight movement 2r + 2c test")
        void moveUnsuccessMoveTest() {
            CellPosition testKnightPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,5)
                    .with(WHITE, KNIGHT).at(testKnightPosition)
                    .build();

            KnightData knightData = (KnightData) gameData.getFigureAt(testKnightPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKnightPosition.shift(2, 2))
                    .build();

            ActionResult result = knightData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Impossible move", result);
        }

        @Test
        @Order(5)
        @DisplayName("Knight movement to cell occupied by Ally test")
        void moveOnCellOccupiedByAllyTest() {
            CellPosition testKnightPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(4,7)
                    .with(WHITE, KNIGHT).at(testKnightPosition)
                    .build();

            KnightData knightData = (KnightData) gameData.getFigureAt(testKnightPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(new CellPosition(4, 7))
                    .build();

            ActionResult result = knightData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Target cell is occupied by ally", result);
        }

        @Test
        @Order(6)
        @DisplayName("Knight movement on occupied by Enemy test")
        void moveOnCellOccupiedByEnemyTest() {
            CellPosition testKnightPosition = new CellPosition(5, 5);
            CellPosition testBishopPosition = new CellPosition(4, 7);

            GameData gameData = prepareBoard()
                    .with(BLACK, BISHOP).at(testBishopPosition)
                    .with(WHITE, KNIGHT).at(testKnightPosition)
                    .build();

            KnightData knightData = (KnightData) gameData.getFigureAt(testKnightPosition);
            BishopData bishopData = (BishopData) gameData.getFigureAt(testBishopPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testBishopPosition)
                    .build();

            ActionResult result = knightData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertKill(bishopData, result.getUpdates().get(0));
            assertMove(knightData, testBishopPosition, result.getUpdates().get(1));
        }

        @Test
        @Order(7)
        @DisplayName("Knight movement when King is under attack test")
        void moveWhenKingUnderAttackTest() {
            CellPosition testBishopPosition = new CellPosition(7, 7);
            CellPosition testKnightPosition = testBishopPosition.shift(-3, -3);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testBishopPosition.shift(-5,-5))
                    .with(BLACK, BISHOP).at(testBishopPosition)
                    .with(WHITE, KNIGHT).at(testKnightPosition)
                    .build();

            KnightData knightData = (KnightData) gameData.getFigureAt(testKnightPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testKnightPosition.shift(2, 1))
                    .build();

            ActionResult result = knightData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("King is under attack", result);
        }

    }

    @Nested
    @Order(2)
    @DisplayName("Knight isUnderAttack tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class KnightIsUnderAttackTest {
        @Test
        @Order(1)
        @DisplayName("Check attacking up-left-left cells")
        void isUnderAttackUpLeftLeftTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testKnightPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, KNIGHT).at(testKnightPosition)
                    .build();

            KnightData knightData = (KnightData) gameData.getFigureAt(testKnightPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = knightData.isUnderAttack(gameData, testKnightPosition.shift(1, -2), kingData, kingData.getPosition());

            assertTrue(isAttacked);
        }

        @Test
        @Order(2)
        @DisplayName("Check attacking right-up-up cells")
        void isUnderAttackRightUpUpTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testKnightPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, KNIGHT).at(testKnightPosition)
                    .build();

            KnightData knightData = (KnightData) gameData.getFigureAt(testKnightPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = knightData.isUnderAttack(gameData, testKnightPosition.shift(2, 1), kingData, kingData.getPosition());

            assertTrue(isAttacked);
        }

        @Test
        @Order(3)
        @DisplayName("Check attacking impossible move cells")
        void isUnderAttackRightTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testKnightPosition = new CellPosition(5, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, KNIGHT).at(testKnightPosition)
                    .build();

            KnightData knightData = (KnightData) gameData.getFigureAt(testKnightPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = knightData.isUnderAttack(gameData, testKnightPosition.shift(2, 2), kingData, kingData.getPosition());

            assertFalse(isAttacked);
        }

    }

}
