package com.nargiz.chess.shared.command.response;

import com.nargiz.chess.shared.ioc.anotation.Command;
import com.nargiz.chess.shared.models.FigureData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Command
public class ErrorResponse extends ChessCommandResponse {
    private String message;
}
