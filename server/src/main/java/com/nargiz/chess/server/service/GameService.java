package com.nargiz.chess.server.service;

import com.nargiz.chess.shared.command.ActionCommand;
import com.nargiz.chess.shared.models.GameData;

import java.util.UUID;

public interface GameService {
    void createLobby(UUID hostId, String name, String hostName, int memberCount);

    void connect(UUID hostId, UUID guestId, String guestName);

    void removeMember(UUID memberId);

    GameData startGame(UUID userId);

    String getHostName(UUID hostId);

    boolean isHost(UUID userId);

    void performGameAction(ActionCommand command);
}
