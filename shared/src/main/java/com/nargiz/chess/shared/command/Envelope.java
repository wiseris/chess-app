package com.nargiz.chess.shared.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Envelope {
    String command;
    Map<String, Object> body = new HashMap<>();
}
