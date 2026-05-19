package com.nargiz.chess.server.repository.impl;

import com.nargiz.chess.server.exceptions.ServiceException;
import com.nargiz.chess.server.repository.ServerRepository;
import com.nargiz.chess.shared.ioc.anotation.Component;
import com.nargiz.chess.shared.models.*;
import com.nargiz.chess.shared.models.enums.ColorType;
import com.nargiz.chess.shared.models.enums.FigureType;
import com.nargiz.chess.shared.models.enums.GameState;
import com.nargiz.chess.shared.models.enums.MemberStatus;
import com.nargiz.chess.shared.models.figures.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nargiz.chess.shared.models.enums.ColorType.*;
import static com.nargiz.chess.shared.models.enums.FigureType.*;

@Component
public class ServerRepositoryImpl implements ServerRepository {
    Map<UUID, LobbyData> lobbyByMemberMap = new ConcurrentHashMap<>();

    @Override
    public LobbyData createLobby(UUID hostId, String lobbyName, String hostName, int memberCount) {
        MemberData hostMember = MemberData.builder()
                .id(hostId)
                .name(hostName)
                .state(MemberStatus.READY)
                .build();

        LobbyData lobbyData = LobbyData.builder()
                .name(lobbyName)
                .host(hostMember)
                .maxMemberCount(memberCount)
                .members(new HashMap<>())
                .build();
        lobbyByMemberMap.put(hostMember.getId(), lobbyData);
        System.out.println("Lobby is added: " + lobbyData);
        return lobbyData;
    }

    public Collection<LobbyData> getLobbies() {
        return new HashSet<>(lobbyByMemberMap.values());
    }

    @Override
    public void destroyLobby(UUID memberId) {
        lobbyByMemberMap.remove(memberId);
    }

    @Override
    public LobbyData getLobbyByMember(UUID memberId) {
        return lobbyByMemberMap.get(memberId);
    }

    @Override
    public GameData createGame(UUID guestId) {
        LobbyData lobbyData = getLobbyByMember(guestId);
        MemberData white = lobbyData.getHost();
        MemberData black = lobbyData.getMembers().get(guestId);

        boolean hostIsNotWhite = Math.random() > 0.5;
        if (hostIsNotWhite) {
           MemberData temp = white;
           white = black;
           black = temp;
        }

        Map<CellPosition, FigureData> figures = initFigures(white, black);

        GameData gameData = GameData.builder()
                .black(black)
                .white(white)
                .state(GameState.PLAYING)
                .figures(figures)
                .history(new ArrayList<>())
                .build();
        lobbyData.setGame(gameData);

        return gameData;
    }

    private Map<CellPosition, FigureData> initFigures(MemberData white, MemberData black) {
        List<FigureData> figures = new ArrayList<>();

        RookData rightWhiteRook = (RookData) createFigure(WHITE, ROOK).at(1, 8);
        RookData leftWhiteRook = (RookData) createFigure(WHITE, ROOK).at(1, 1);
        KingData whiteKing = KingData.builder()
                .rightRook(rightWhiteRook)
                .leftRook(leftWhiteRook)
                .color(WHITE)
                .position(new CellPosition(1, 5))
                .build();

        RookData rightBlackRook = (RookData) createFigure(BLACK, ROOK).at(8, 8);
        RookData leftBlackRook = (RookData) createFigure(BLACK, ROOK).at(8, 1);
        KingData blackKing = KingData.builder()
                .rightRook(rightBlackRook)
                .leftRook(leftBlackRook)
                .color(BLACK)
                .position(new CellPosition(8, 5))
                .build();

        white.setKing(whiteKing);
        black.setKing(blackKing);

        figures.addAll(List.of(
                whiteKing,
                leftWhiteRook,
                rightWhiteRook,
                blackKing,
                leftBlackRook,
                rightBlackRook
        ));
        figures.add(createFigure(WHITE, KNIGHT).at(1, 2));
        figures.add(createFigure(WHITE, KNIGHT).at(1, 7));
        figures.add(createFigure(WHITE, BISHOP).at(1, 3));
        figures.add(createFigure(WHITE, BISHOP).at(1, 6));
        figures.add(createFigure(WHITE, QUEEN).at(1, 4));

        figures.add(createFigure(BLACK, KNIGHT).at(8, 2));
        figures.add(createFigure(BLACK, KNIGHT).at(8, 7));
        figures.add(createFigure(BLACK, BISHOP).at(8, 3));
        figures.add(createFigure(BLACK, BISHOP).at(8, 6));
        figures.add(createFigure(BLACK, QUEEN).at(8, 4));

        for (int column = 1; column <= 8; column++) {
            figures.add(createFigure(WHITE, PAWN).at(2, column));
            figures.add(createFigure(BLACK, PAWN).at(7, column));
        }

        return new HashMap<>(
                figures.stream().collect(
                    Collectors.toMap(
                            FigureData::getPosition,
                            Function.identity()
                    )
                )
        );
    }

    private FigureData createFigure(ColorType color, FigureType type) {
        FigureData figureData = switch (type) {
            case ROOK -> new RookData();
            case KNIGHT -> new KnightData();
            case BISHOP -> new BishopData();
            case QUEEN -> new QueenData();
            case KING -> new KingData();
            case PAWN -> new PawnData();
        };
        figureData.setColor(color);
        figureData.setMoved(false);
        figureData.setDead(false);
        return figureData;
    }

    @Override
    public GameData getGame(UUID guestId) {
        return lobbyByMemberMap.get(guestId).getGame();
    }

    @Override
    public MemberData getHost(UUID guestId) {
        return lobbyByMemberMap.get(guestId).getHost();
    }

    @Override
    public void addMember(UUID hostId, UUID guestId, String guestName) {
        LobbyData lobbyData = lobbyByMemberMap.get(hostId);
        synchronized (lobbyData.getMembers()) {
            if (lobbyData.getMaxMemberCount() <= lobbyData.getMembers().size()) {
                throw new ServiceException("The maximum member count has been reached");
            }

            MemberData newMember = MemberData.builder()
                    .id(guestId)
                    .name(guestName)
                    .state(MemberStatus.READY)
                    .build();
            lobbyData.getMembers().put(newMember.getId(), newMember);
            lobbyByMemberMap.put(newMember.getId(), lobbyData);
        }
    }

    @Override
    public void removeMember(UUID guestId) {
        LobbyData lobbyData = lobbyByMemberMap.get(guestId);
        if (lobbyData == null) {
            return;
        }
        synchronized (lobbyData.getMembers()) {
            lobbyData.getMembers().remove(guestId);
            lobbyByMemberMap.remove(guestId);
        }
    }

    @Override
    public Set<MemberData> getMemberList(UUID guestId) {
        LobbyData lobbyData = lobbyByMemberMap.get(guestId);
        if (lobbyData == null) {
            return Collections.EMPTY_SET;
        }
        return new HashSet<>(lobbyData.getMembers().values());
    }

    @Override
    public List<HistoryData> getHistory(UUID guestId) {
        LobbyData lobbyData = lobbyByMemberMap.get(guestId);
        if (lobbyData == null || lobbyData.getGame() == null) {
            return Collections.EMPTY_LIST;
        }
        return lobbyData.getGame().getHistory();
    }

    @Override
    public Map<CellPosition, FigureData> getFigures(UUID guestId) {
        LobbyData lobbyData = lobbyByMemberMap.get(guestId);
        if (lobbyData == null || lobbyData.getGame() == null) {
            return Collections.EMPTY_MAP;
        }
        return lobbyData.getGame().getFigures();
    }
}
