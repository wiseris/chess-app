package com.nargiz.chess.client.model;

import com.nargiz.chess.shared.models.CellPosition;
import com.nargiz.chess.shared.models.enums.ColorType;
import com.nargiz.chess.shared.models.enums.FigureType;
import javafx.scene.image.ImageView;

import java.util.UUID;

public class Figure {
    private final FigureType type;
    private UUID id;
    private CellPosition position;
    private ImageView image;
    private ColorType color;

    public Figure(UUID id, CellPosition position, ImageView image, ColorType color, FigureType type) {
        this.id = id;
        this.position = position;
        this.image = image;
        this.color = color;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public CellPosition getPosition() {
        return position;
    }

    public void setPosition(CellPosition position) {
        this.position = position;
    }

    public ImageView getImage() {
        return image;
    }

    public boolean isMine(ColorType color) {
        return this.color.equals(color);
    }

    public FigureType getType() {
        return type;
    }
}
