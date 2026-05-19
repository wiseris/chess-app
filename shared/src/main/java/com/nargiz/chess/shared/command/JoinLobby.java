package com.nargiz.chess.shared.command;

import com.nargiz.chess.shared.ioc.anotation.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Getter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Command
public class JoinLobby extends ChessCommand {
    String memberName;
    UUID hostId;
}
