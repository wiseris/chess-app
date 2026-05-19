package com.nargiz.chess.shared.models;

import com.nargiz.chess.shared.models.enums.ActionType;
import com.nargiz.chess.shared.models.enums.ColorType;
import com.nargiz.chess.shared.models.enums.FigureType;
import com.nargiz.chess.shared.models.enums.MemberStatus;
import com.nargiz.chess.shared.models.figures.KingData;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberData {
    private UUID id;
    private String name;
    private MemberStatus state;
    private KingData king;
}
