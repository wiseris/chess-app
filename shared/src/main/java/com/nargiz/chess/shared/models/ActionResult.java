package com.nargiz.chess.shared.models;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class ActionResult {
    List<ValidationError> errors = new ArrayList<>();
    List<ActionPerform> updates = new ArrayList<>();

    public void append(ActionResult result) {
        errors.addAll(result.errors);
        updates.addAll(result.updates);
    }

    public void addError(ValidationError error) {
        errors.add(error);
    }

    public void addAction(ActionPerform actionPerform) {
        updates.add(actionPerform);
    }
}
