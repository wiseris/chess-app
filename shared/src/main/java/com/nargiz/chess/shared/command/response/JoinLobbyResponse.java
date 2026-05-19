package com.nargiz.chess.shared.command.response;

import com.nargiz.chess.shared.ioc.anotation.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Command
public class JoinLobbyResponse extends ChessCommandResponse {
    private UUID userId;
    private String hostName;
    private String message;
}
