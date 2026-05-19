package com.nargiz.chess.shared.command;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Setter
@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChessCommand {
    @Builder.Default
    UUID commandId = UUID.randomUUID();
    UUID userId;
}
