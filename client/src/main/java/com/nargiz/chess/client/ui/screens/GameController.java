package com.nargiz.chess.client.ui.screens;

import com.nargiz.chess.client.model.Figure;
import com.nargiz.chess.client.model.events.ErrorEvent;
import com.nargiz.chess.client.model.events.StartGameEvent;
import com.nargiz.chess.client.model.events.UpdateGameStateEvent;
import com.nargiz.chess.client.network.TCPClient;
import com.nargiz.chess.client.ui.BaseController;
import com.nargiz.chess.client.ui.dialogs.DialogSelectTransformationController;
import com.nargiz.chess.shared.command.ActionCommand;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import com.nargiz.chess.shared.models.CellPosition;
import com.nargiz.chess.shared.models.FigureData;
import com.nargiz.chess.shared.models.HistoryData;
import com.nargiz.chess.shared.models.enums.ColorType;
import com.nargiz.chess.shared.models.enums.FigureType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

import java.util.*;
import java.util.stream.Collectors;

import static com.nargiz.chess.shared.models.enums.ColorType.BLACK;
import static com.nargiz.chess.shared.models.enums.ColorType.WHITE;
import static com.nargiz.chess.shared.models.enums.FigureType.PAWN;

@Component
public class GameController extends BaseController {

    public static final String FIGURE_ID = "FIGURE_ID";
    @Inject
    MainScreenController mainScreenController;

    @Inject
    DialogSelectTransformationController selectTransformationController;

    @Inject
    ApplicationEventBus applicationEventBus;

    @Inject
    TCPClient tcpClient;

    double baseSceneSizeX = 662;
    double baseSceneSizeY = 485;
    double baseBoardSizeX = 350;

    Scale scaleObj = new Scale(1, 1, 0, 0);

    @FXML
    ImageView boardImage;

    @FXML
    GridPane boardPane;

    @FXML
    VBox gameBoard;

    @FXML
    Label whitePlayerName;

    @FXML
    Label blackPlayerName;

    @FXML
    ListView<HistoryData> history;

    private ColorType playerColor;

    private Map<CellPosition, Figure> figurePositionMap = new HashMap<>();
    private Map<UUID, Figure> figureIdMap = new HashMap<>();

    @Override
    protected String getFormUrl() {
        return "/forms/Game.fxml";
    }

    @FXML
    public void initialize() {
        gameBoard.getTransforms().add(scaleObj);

        boardPane.setOnDragOver(this::onDragOver);
        boardPane.setOnDragDropped(this::onDragDropped);

        history.setCellFactory(lv -> new ListCell<HistoryData>() {
            @Override
            protected void updateItem(HistoryData historyData, boolean empty) {
                super.updateItem(historyData, empty);
                setText((empty || historyData == null) ? null : historyData.notation());
            }
        });

    }

    private void createFigure(FigureData data) {
        String figurePattern = switch (data.getColor()) {
            case WHITE -> "/images/w%s.png";
            case BLACK -> "/images/b%s.png";
        };

        ImageView image = new ImageView(switch (data.getFigureType()) {
                    case KING -> figurePattern.formatted("king");
                    case QUEEN -> figurePattern.formatted("queen");
                    case BISHOP -> figurePattern.formatted("bishop");
                    case KNIGHT -> figurePattern.formatted("knight");
                    case ROOK -> figurePattern.formatted("rook");
                    case PAWN -> figurePattern.formatted("pawn");
        });

        image.fitHeightProperty().set(25);
        image.fitWidthProperty().set(25);

        Figure figure = new Figure(data.getId(), data.getPosition(), image, data.getColor(), data.getFigureType());
        GridPane.setColumnIndex(image, data.getPosition().getColumn() - 1);
        GridPane.setRowIndex(image, data.getPosition().getRow() - 1);
        GridPane.setValignment(image, VPos.CENTER);
        GridPane.setHalignment(image, HPos.CENTER);
        image.getProperties().put(FIGURE_ID, figure.getId());

        figurePositionMap.put(figure.getPosition(), figure);
        figureIdMap.put(figure.getId(), figure);

        boardPane.add(image, data.getPosition().getColumn() - 1, 8 - data.getPosition().getRow());
        image.setOnDragDetected(this::onDragDetected);
        image.setOnDragDone(this::onDragDone);
    }

    public void update(FigureData figureData) {
        update(figureData.getId(), figureData.getPosition());
    }

    public void update(UUID id, CellPosition newPosition) {
        Figure figure = figureIdMap.get(id);
        figurePositionMap.remove(figure.getPosition());
        figurePositionMap.put(newPosition, figure);

        figure.setPosition(newPosition);

        GridPane.setColumnIndex(figure.getImage(), newPosition.getColumn() - 1);
        GridPane.setRowIndex(figure.getImage(), 8 - newPosition.getRow());
    }

    public void delete(UUID id) {
        Figure figure = figureIdMap.get(id);
        figurePositionMap.remove(figure.getPosition());
        figureIdMap.remove(figure.getId());

        boardPane.getChildren().remove(figure.getImage());
    }

    public void updateFigures(Collection<FigureData> figures) {
        Set<UUID> ids = figures.stream()
                .map(FigureData::getId)
                .collect(Collectors.toUnmodifiableSet());
        Set<UUID> deletedFigures = new HashSet<UUID>(figureIdMap.keySet());
        deletedFigures.removeAll(ids);
        deletedFigures.forEach(this::delete);

        figures.stream()
                .filter(f -> !figureIdMap.containsKey(f.getId()))
                .forEach(this::createFigure);

        figures.forEach(this::update);
    }

    @PostConstruct
    @Override
    public void loadScene() {
        super.loadScene();

        if (scene != null) {
            scene.widthProperty().addListener((obs, oldVal, newVal) -> {
                resizeBoard(newVal.doubleValue(), scene.getHeight());
            });

            scene.heightProperty().addListener((obs, oldVal, newVal) -> {
                resizeBoard(scene.getWidth(), newVal.doubleValue());
            });
        }
    }

    @FXML
    public void exit() {
        navigateTo(mainScreenController);
    }

    private void resizeBoard(double width, double height) {
        double scaleX = width / baseSceneSizeX;
        double scaleY = height / baseSceneSizeY;
        double scale = Math.min(scaleX, scaleY);
        gameBoard.setPrefWidth((width - 312) / scale);
        scaleObj.setX(scale);
        scaleObj.setY(scale);
    }

    private void onDragOver(DragEvent e) {
        if (e.getDragboard().hasImage()) {
            e.acceptTransferModes(TransferMode.MOVE);
        }
        e.consume();
    }

    private void onDragDropped(DragEvent e) {
        if (e.getDragboard().hasImage() && e.getGestureSource() instanceof ImageView sourceImage) {
            ImageView source = (ImageView) e.getGestureSource();
            GridPane oldParent = (GridPane) source.getParent();
            int cellSize = (330 - 17 * 2) / 8;
            int col = (int)(e.getX() - 17) / cellSize;
            int row = (int)(e.getY() - 17) / cellSize;
            UUID id = (UUID) sourceImage.getProperties().get(FIGURE_ID);

            Figure figure = figureIdMap.get(id);
            CellPosition newPosition = new CellPosition(8 - row, col + 1);
            CellPosition oldPosition = figure.getPosition();
            update(id, newPosition);

            if (PAWN.equals(figure.getType()) && (newPosition.getRow() == 1 || newPosition.getRow() == 8)) {
                navigateToAndWait(selectTransformationController);
            }

            tcpClient.send(
                    ActionCommand.builder()
                            .transform(selectTransformationController.getSelected())
                            .fromPosition(oldPosition)
                            .toPosition(newPosition)
                            .build()
                    );

            System.out.printf("Position: %s, %s%n", col, row);
            e.setDropCompleted(true);
        }
        e.consume();
    }

    private void onDragDetected(MouseEvent e) {
        if (e.getSource() instanceof ImageView imageView && playerColor != null) {
            UUID id = (UUID) imageView.getProperties().get(FIGURE_ID);
            Figure figure = figureIdMap.get(id);
            if (!figure.isMine(playerColor) || !isMyTurn()) {
                return;
            }

            Dragboard db = imageView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(imageView.getImage());
            db.setContent(content);
            db.setDragView(
                    imageView.getImage(),
                    imageView.getImage().getWidth()/2,
                    imageView.getImage().getHeight()/2
            );
            imageView.setOpacity(0.5);
            e.consume();
        }
    }

    private boolean isMyTurn() {

        HistoryData lastAction = history.getItems().isEmpty() ? null : history.getItems().getLast();
        return (WHITE.equals(playerColor) && lastAction == null)
                || (lastAction != null && !lastAction.getColor().equals(playerColor));
    }

    private void onDragDone(DragEvent e) {
        if (e.getSource() instanceof ImageView imageView) {
            imageView.setOpacity(1.0);
        }
        e.consume();
    }

    private void onStartGame(StartGameEvent event) {
        Platform.runLater(() -> {
            if (event.getWhiteUserId().equals(tcpClient.getUserId())) {
                playerColor = WHITE;
            }
            if (event.getBlackUserId().equals(tcpClient.getUserId())) {
                playerColor = BLACK;
            }
            whitePlayerName.setText(event.getWhitePlayerName());
            blackPlayerName.setText(event.getBlackPlayerName());
            updateFigures(event.getFigures());
        });
    }

    @PostConstruct
    private void initEvents() {
        applicationEventBus.subscribeOn(StartGameEvent.class, this::onStartGame);
        applicationEventBus.subscribeOn(UpdateGameStateEvent.class, this::onGameStateChange);
    }

    private void onGameStateChange(UpdateGameStateEvent event) {
        Platform.runLater(() -> {
            updateFigures(event.getFigures());

            history.getItems().clear();
            event.getHistoryData().forEach(history.getItems()::add);
        });
    }

}
