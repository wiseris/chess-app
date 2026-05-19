package com.nargiz.chess.server.service.impl;

import com.nargiz.chess.server.events.MemberDisconnectedEvent;
import com.nargiz.chess.server.exceptions.ServiceException;
import com.nargiz.chess.server.network.TCPServer;
import com.nargiz.chess.server.repository.ServerRepository;
import com.nargiz.chess.server.service.GameService;
import com.nargiz.chess.shared.command.ActionCommand;
import com.nargiz.chess.shared.command.ChessCommand;
import com.nargiz.chess.shared.command.UpdateGameState;
import com.nargiz.chess.shared.command.UpdateMembers;
import com.nargiz.chess.shared.command.response.StartGameResponse;
import com.nargiz.chess.shared.events.ApplicationEventBus;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.ioc.anotation.Inject;
import com.nargiz.chess.shared.ioc.anotation.PostConstruct;
import com.nargiz.chess.shared.models.*;
import com.nargiz.chess.shared.models.actions.KillFigure;
import com.nargiz.chess.shared.models.actions.TransformFigure;
import com.nargiz.chess.shared.models.enums.ActionType;
import com.nargiz.chess.shared.models.enums.FigureType;
import com.nargiz.chess.shared.models.enums.GameState;
import com.nargiz.chess.shared.models.figures.KingData;

import java.util.*;
import java.util.stream.Collectors;

import static com.nargiz.chess.shared.models.enums.ColorType.WHITE;

@Component
public class GameServiceImpl implements GameService {

    @Inject
    ApplicationEventBus eventBus;

    @Inject
    ServerRepository serverRepository;

    @Inject
    TCPServer server;

    @Override
    public void createLobby(UUID hostId, String name, String hostName, int memberCount) {
        serverRepository.createLobby(hostId, name, hostName, memberCount);
    }

    @Override
    public void connect(UUID hostId, UUID guestId, String guestName) {
        serverRepository.addMember(hostId, guestId, guestName);

        Set<MemberData> members = serverRepository.getMemberList(hostId);
        broadcastLobby(hostId, new UpdateMembers(members));
    }

    @Override
    public void removeMember(UUID memberId) {
        LobbyData lobby = serverRepository.getLobbyByMember(memberId);

        serverRepository.removeMember(memberId);

        Set<MemberData> members = new HashSet<>(lobby.getMembers().values());
        broadcastLobby(memberId, new UpdateMembers(members));
    }

    @Override
    public GameData startGame(UUID userId) {
        GameData gameData = serverRepository.createGame(userId);
        broadcastLobby(userId,
            StartGameResponse.builder()
                    .whiteUserId(gameData.getWhite().getId())
                    .blackUserId(gameData.getBlack().getId())
                    .whitePlayerName(gameData.getWhite().getName())
                .blackPlayerName(gameData.getBlack().getName())
                .figures(gameData.getFigures().values())
                .build()
        );

        return gameData;
    }

    @Override
    public String getHostName(UUID hostId) {
        return serverRepository.getHost(hostId).getName();
    }

    @Override
    public boolean isHost(UUID userId) {
        MemberData host = serverRepository.getHost(userId);
        return host != null && host.getId().equals(userId);
    }

    @Override
    public void performGameAction(ActionCommand command) {
        GameData gameData = serverRepository.getGame(command.getUserId());
        if (gameData == null) {
            throw new ServiceException("Game is not found");
        }
        synchronized (gameData) {
            try {
                validateCommand(command, gameData);

                FigureData actionFigure = gameData.getFigureAt(command.getFromPosition());
                if (actionFigure == null || actionFigure.isDead() || !actionFigure.getColor().equals(gameData.getCurrentColor())) {
                    throw new ServiceException("Wrong position");
                }

                ActionResult result = actionFigure.validateAction(gameData, command);
                if (!result.getErrors().isEmpty()) {
                    throw new ServiceException(result.getErrors().stream().map(ValidationError::getMessage).collect(Collectors.joining("\n")));
                }

                result.getUpdates().forEach(action -> action.doAction(gameData));

                HistoryData historyData = createHistory(gameData, command, actionFigure, result);

                boolean looksLikeDraw = gameData.getFigures().values().stream()
                        .filter(f -> f.isOpponentColor(actionFigure))
                        .allMatch(f -> f.isLocked(gameData));

                if (historyData.isMate()) {
                    gameData.setState(WHITE.equals(gameData.getCurrentColor()) ? GameState.WHITE_WINS : GameState.BLACK_WINS);
                }

                gameData.getHistory().add(historyData);

                broadcastLobby(command.getUserId(),
                        UpdateGameState.builder()
                                .figures(gameData.getFigures().values())
                                .historyData(gameData.getHistory())
                                .build()
                );
            } catch (Exception e) {
                restoreBoard(command.getUserId(), gameData);
                throw e;
            }
        }
    }

    private static HistoryData createHistory(GameData gameData, ActionCommand command, FigureData actionFigure, ActionResult result) {
        HistoryData historyData = HistoryData.builder()
                .color(actionFigure.getColor())
                .figure(actionFigure.getFigureType())
                .from(command.getFromPosition())
                .to(command.getToPosition())
                .action(ActionType.MOVE)
                .build();

        if (FigureType.KING.equals(actionFigure.getFigureType())) {
            historyData.setShortCastling(command.getToPosition().getColumn() - command.getFromPosition().getColumn() > 1);
            historyData.setLongCastling(command.getToPosition().getColumn() - command.getFromPosition().getColumn() < -1);
        }

        KillFigure killFigureAction = result.getUpdates().stream()
                .filter(action -> action instanceof KillFigure)
                .map(action -> (KillFigure) action)
                .findFirst()
                .orElse(null);

        if (killFigureAction != null) {
            historyData.setAction(ActionType.ATTACK);
            historyData.setEnPassant(killFigureAction.isEnPassant());
        }

        TransformFigure transformFigure = result.getUpdates().stream()
                .filter(action -> action instanceof TransformFigure)
                .map(action -> (TransformFigure) action)
                .findFirst()
                .orElse(null);

        if (transformFigure != null) {
            historyData.setTransform(transformFigure.getFigureType());
        }

        MemberData opponent = gameData.getOpponentPlayer();
        KingData opponentKing = opponent.getKing();
        boolean isCheck = gameData.getFigures().values().stream()
                .filter(FigureData::isAlive)
                .filter(f -> f.isOpponentColor(opponentKing))
                .anyMatch(f -> f.isUnderAttack(gameData, opponentKing.getPosition(), opponentKing, opponentKing.getPosition()));
        historyData.setCheck(isCheck);

        if (isCheck) {
            boolean isAttackerUnderAttack = gameData.getFigures().values().stream()
                    .filter(f -> f.isOpponentColor(actionFigure))
                    .anyMatch(f -> f.isUnderAttack(gameData, actionFigure.getPosition(), actionFigure, actionFigure.getPosition()));
            historyData.setMate(!isAttackerUnderAttack && opponentKing.isLocked(gameData));
        }

        return historyData;
    }

    private static void validateCommand(ActionCommand command, GameData gameData) {
        if (!GameState.PLAYING.equals(gameData.getState())) {
            throw new ServiceException("Wrong game state: " + gameData.getState());
        }

        if (
                !gameData.getWhite().getId().equals(command.getUserId())
                        && !gameData.getBlack().getId().equals(command.getUserId())
        ) {
            throw new ServiceException("Access denied. Wrong player");
        }

        if (!gameData.getCurrentPlayer().getId().equals(command.getUserId())) {
            throw new ServiceException("Wrong turn");
        }

        if (FigureData.isOutOfBoard(command.getFromPosition())) {
            throw new ServiceException("Wrong starting position");
        }

        if (FigureData.isOutOfBoard(command.getToPosition())) {
            throw new ServiceException("Wrong end position");
        }
    }

    private void removeMember(MemberDisconnectedEvent memberDisconnectedEvent) {
        removeMember(memberDisconnectedEvent.getMemberId());
    }

    private void restoreBoard(UUID memberId, GameData gameData) {
        server.broadcast(Set.of(memberId),
                UpdateGameState.builder()
                        .figures(gameData.getFigures().values())
                        .historyData(gameData.getHistory())
                        .build()
        );
    }

    private void broadcastLobby(UUID memberId, ChessCommand command) {
        LobbyData lobby = serverRepository.getLobbyByMember(memberId);
        Set<UUID> members = new HashSet<>(lobby.getMembers().keySet());
        members.add(lobby.getHost().getId());
        server.broadcast(members, command);
    }

    @PostConstruct
    private void init() {
        eventBus.subscribeOn(
                MemberDisconnectedEvent.class,
                this::removeMember
        );
    }

}
