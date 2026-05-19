package com.nargiz.chess.client.model.events;

import com.nargiz.chess.shared.events.GameEvent;
import com.nargiz.chess.shared.models.MemberData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class UpdateMembersEvent implements GameEvent {
    private Set<MemberData> members;
}
