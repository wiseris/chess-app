package com.nargiz.chess.shared.models;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LobbyData {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String name;
    private int maxMemberCount;
    private MemberData host;
    private Map<UUID, MemberData> members;
    private GameData game;

}
