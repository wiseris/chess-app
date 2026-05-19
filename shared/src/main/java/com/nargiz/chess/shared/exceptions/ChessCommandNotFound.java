package com.nargiz.chess.shared.exceptions;

import java.text.MessageFormat;

public class ChessCommandNotFound extends RuntimeException {
    public ChessCommandNotFound(String command) {
        super(MessageFormat.format("Chess command not found: {0}", command));
    }
}
