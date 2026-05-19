package com.nargiz.chess.shared.command.response;

import com.nargiz.chess.shared.ioc.anotation.Command;
import com.nargiz.chess.shared.models.CellPosition;
import com.nargiz.chess.shared.models.FigureData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Command
public class StartGameResponse extends ChessCommandResponse {
    private UUID whiteUserId;
    private UUID blackUserId;
    private String whitePlayerName;
    private String blackPlayerName;
    private Collection<FigureData> figures;
}
