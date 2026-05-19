package com.nargiz.chess.shared.command;

import com.nargiz.chess.shared.ioc.anotation.Command;
import com.nargiz.chess.shared.models.FigureData;
import com.nargiz.chess.shared.models.HistoryData;
import com.nargiz.chess.shared.models.MemberData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Getter
@Jacksonized
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Command
public class UpdateGameState extends ChessCommand {
    Collection<FigureData> figures;
    Collection<HistoryData> historyData;
}
