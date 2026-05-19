package com.nargiz.chess.shared.models.figures;

import com.nargiz.chess.shared.command.ActionCommand;
import com.nargiz.chess.shared.models.*;
import com.nargiz.chess.shared.models.actions.KillFigure;
import com.nargiz.chess.shared.models.actions.MoveFigure;
import com.nargiz.chess.shared.models.actions.TransformFigure;
import com.nargiz.chess.shared.models.enums.ColorType;
import com.nargiz.chess.shared.models.enums.FigureType;
import org.junit.jupiter.api.*;

import java.util.List;

import static com.nargiz.chess.shared.models.enums.ColorType.BLACK;
import static com.nargiz.chess.shared.models.enums.ColorType.WHITE;
import static com.nargiz.chess.shared.models.enums.FigureType.*;
import static org.junit.jupiter.api.Assertions.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class PawnDataTest extends BaseFigureTest  {

    @Nested
    @Order(1)
    @DisplayName("Pawn ValidateAction tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class PawnValidateActionTest {
        @Test
        @Order(1)
        @DisplayName("Pawn movement one step test")
        void moveSuccessVerticalTest() {
            CellPosition testPawnPosition = new CellPosition(2, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,5)
                    .with(WHITE, PAWN).at(testPawnPosition)
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(new CellPosition(3, 5))
                    .build();

            ActionResult result = pawnData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(pawnData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(2)
        @DisplayName("Pawn movement two steps test")
        void moveSuccessVertical2Test() {
            CellPosition testPawnPosition = new CellPosition(2, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,5)
                    .with(WHITE, PAWN).at(testPawnPosition)
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(new CellPosition(4, 5))
                    .build();

            ActionResult result = pawnData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(pawnData, actionCommand.getToPosition(), result.getUpdates().getFirst());
        }

        @Test
        @Order(3)
        @DisplayName("Pawn movement two steps when already moved test")
        void moveFailVertical2WhenMovedTest() {
            CellPosition testPawnPosition = new CellPosition(2, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,5)
                    .with(WHITE, PAWN).at(testPawnPosition).moved()
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(new CellPosition(4, 5))
                    .build();

            ActionResult result = pawnData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Pawn is already moved", result);
        }

        @Test
        @Order(4)
        @DisplayName("Pawn movement two steps when King On The Way test")
        void moveKingOnTheWayTest() {
            CellPosition testPawnPosition = new CellPosition(2, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(3,5)
                    .with(WHITE, PAWN).at(testPawnPosition)
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(new CellPosition(4, 5))
                    .build();

            ActionResult result = pawnData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Impossible move", result);
        }

        @Test
        @Order(5)
        @DisplayName("Pawn movement two steps when Cell occupied by Ally test")
        void moveOnCellOccupiedByAllyTest() {
            CellPosition testPawnPosition = new CellPosition(2, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(4,5)
                    .with(WHITE, PAWN).at(testPawnPosition)
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(new CellPosition(4, 5))
                    .build();

            ActionResult result = pawnData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Target cell is occupied", result);
        }

        @Test
        @Order(6)
        @DisplayName("Pawn movement two steps when Cell occupied by Enemy test")
        void moveOnCellOccupiedByEnemyTest() {
            CellPosition testPawnPosition = new CellPosition(2, 5);

            GameData gameData = prepareBoard()
                    .with(BLACK, KING).at(4,5)
                    .with(WHITE, PAWN).at(testPawnPosition)
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(new CellPosition(4, 5))
                    .build();

            ActionResult result = pawnData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertError("Target cell is occupied", result);
        }

        @Test
        @Order(7)
        @DisplayName("Pawn movement attack test")
        void moveAttackTest() {
            CellPosition testPawnPosition = new CellPosition(2, 5);
            CellPosition testEnemyPosition = new CellPosition(3, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,5)
                    .with(WHITE, PAWN).at(testPawnPosition)
                    .with(BLACK, BISHOP).at(testEnemyPosition)
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);
            BishopData bishopData = (BishopData) gameData.getFigureAt(testEnemyPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testEnemyPosition)
                    .build();

            ActionResult result = pawnData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertKill(bishopData, result.getUpdates().get(0));
            assertMove(pawnData, actionCommand.getToPosition(), result.getUpdates().get(1));

        }

        @Test
        @Order(8)
        @DisplayName("Pawn movement last line test")
        void moveLastLineTest() {
            CellPosition testPawnPosition = new CellPosition(7, 5);
            CellPosition testTargetPosition = new CellPosition(8, 5);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,5)
                    .with(WHITE, PAWN).at(testPawnPosition)
                    .with(BLACK, BISHOP).at(new CellPosition(8, 4))
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testTargetPosition)
                    .transform(FigureType.QUEEN)
                    .build();

            ActionResult result = pawnData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertMove(pawnData, testTargetPosition, result.getUpdates().get(0));
            assertTransform(pawnData, QUEEN, result.getUpdates().get(1));

        }

        @Test
        @Order(9)
        @DisplayName("Pawn movement attack at last line test")
        void moveAttackLastLineTest() {
            CellPosition testPawnPosition = new CellPosition(7, 5);
            CellPosition testTargetPosition = new CellPosition(8, 4);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(1,5)
                    .with(WHITE, PAWN).at(testPawnPosition)
                    .with(BLACK, BISHOP).at(testTargetPosition)
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);
            BishopData bishopData = (BishopData) gameData.getFigureAt(testTargetPosition);

            ActionCommand actionCommand = ActionCommand.builder()
                    .toPosition(testTargetPosition)
                    .transform(FigureType.QUEEN)
                    .build();

            ActionResult result = pawnData.validateAction(gameData, actionCommand);

            System.out.println(result.toString());

            assertKill(bishopData, result.getUpdates().get(0));
            assertMove(pawnData, testTargetPosition, result.getUpdates().get(1));
            assertTransform(pawnData, QUEEN, result.getUpdates().get(2));
        }
    }

    @Nested
    @Order(2)
    @DisplayName("Pawn isUnderAttack tests")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class PawnIsUnderAttackTest {
        @Test
        @Order(1)
        @DisplayName("Check attacking left-up cells")
        void isUnderAttackLeftUpTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testPawnPosition = new CellPosition(2, 5);
            CellPosition testBishopPosition = new CellPosition(8, 3);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, PAWN).at(testPawnPosition)
                    .with(BLACK, BISHOP).at(testBishopPosition)
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = pawnData.isUnderAttack(gameData, new CellPosition(3, 4), kingData, kingData.getPosition());

            assertTrue(isAttacked);
        }

        @Test
        @Order(2)
        @DisplayName("Check attacking right-up cells")
        void isUnderAttackRightUpTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testPawnPosition = new CellPosition(2, 5);
            CellPosition testBishopPosition = new CellPosition(8, 3);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, PAWN).at(testPawnPosition)
                    .with(BLACK, BISHOP).at(testBishopPosition)
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = pawnData.isUnderAttack(gameData, new CellPosition(3, 6), kingData, kingData.getPosition());

            assertTrue(isAttacked);
        }

        @Test
        @Order(3)
        @DisplayName("Check attacking right cells")
        void isUnderAttackRightTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testPawnPosition = new CellPosition(2, 5);
            CellPosition testBishopPosition = new CellPosition(8, 3);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, PAWN).at(testPawnPosition)
                    .with(BLACK, BISHOP).at(testBishopPosition)
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = pawnData.isUnderAttack(gameData, new CellPosition(2, 6), kingData, kingData.getPosition());

            assertFalse(isAttacked);
        }

        @Test
        @Order(4)
        @DisplayName("Check attacking up cells")
        void isUnderAttackUpTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testPawnPosition = new CellPosition(2, 5);
            CellPosition testBishopPosition = new CellPosition(8, 3);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, PAWN).at(testPawnPosition)
                    .with(BLACK, BISHOP).at(testBishopPosition)
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = pawnData.isUnderAttack(gameData, new CellPosition(3, 5), kingData, kingData.getPosition());

            assertFalse(isAttacked);
        }

        @Test
        @Order(5)
        @DisplayName("Check attacking down-left cells")
        void isUnderAttackDownLeftTest() {
            CellPosition testKingPosition = new CellPosition(1, 5);
            CellPosition testPawnPosition = new CellPosition(2, 5);
            CellPosition testBishopPosition = new CellPosition(8, 3);

            GameData gameData = prepareBoard()
                    .with(WHITE, KING).at(testKingPosition)
                    .with(WHITE, PAWN).at(testPawnPosition)
                    .with(BLACK, BISHOP).at(testBishopPosition)
                    .build();

            PawnData pawnData = (PawnData) gameData.getFigureAt(testPawnPosition);
            KingData kingData = (KingData) gameData.getFigureAt(testKingPosition);

            boolean isAttacked = pawnData.isUnderAttack(gameData, new CellPosition(1, 4), kingData, kingData.getPosition());

            assertFalse(isAttacked);
        }
    }

}
