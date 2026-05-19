package com.nargiz.chess.server.repository;

import com.nargiz.chess.shared.models.*;

import java.util.*;

public interface ServerRepository {
    LobbyData createLobby(UUID hostId, String lobbyName, String hostName, int memberCount);
    void destroyLobby(UUID lobbyId);
    LobbyData getLobbyByMember(UUID memberId);
    Collection<LobbyData> getLobbies();

    GameData createGame(UUID guestId);

    GameData getGame(UUID guestId);

    MemberData getHost(UUID guestId);

    void addMember(UUID hostId, UUID guestId, String guestName);
    void removeMember(UUID guestId);
    Set<MemberData> getMemberList(UUID guestId);

    List<HistoryData> getHistory(UUID guestId);
    Map<CellPosition, FigureData> getFigures(UUID guestId);

}
