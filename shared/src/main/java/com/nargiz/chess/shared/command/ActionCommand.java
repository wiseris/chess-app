package com.nargiz.chess.shared.command;

import com.nargiz.chess.shared.ioc.anotation.Command;
import com.nargiz.chess.shared.models.CellPosition;
import com.nargiz.chess.shared.models.enums.FigureType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Command
public class ActionCommand extends ChessCommand {
    FigureType transform;
    CellPosition fromPosition;
    CellPosition toPosition;
}
