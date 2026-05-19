package com.nargiz.chess.shared.command.response;

import com.nargiz.chess.shared.ioc.anotation.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Command
public class ConnectServerResponse extends ChessCommandResponse {
    private boolean allowed;
    private String message;
}
