package com.nargiz.chess.shared.command.response;

import com.nargiz.chess.shared.command.ChessCommand;
import com.nargiz.chess.shared.ioc.anotation.Command;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Command
public class ChessCommandResponse extends ChessCommand {
}
