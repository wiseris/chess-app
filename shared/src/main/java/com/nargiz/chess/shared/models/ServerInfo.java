package com.nargiz.chess.shared.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ServerInfo {
    private String name;
    private String address;
    private UUID hostId;
    private int port;
    private int currentPlayers;
    private int maxPlayers;
}
