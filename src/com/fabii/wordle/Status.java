package com.fabii.wordle;

import java.awt.*;

public enum Status {
    EXCLUDED,
    CONTAINED,
    CORRECT,
    NOT_EVALUATED;

    public static Color toColor(Status s) {
        return switch (s) {
            case EXCLUDED -> new Color(52, 53, 54, 255);
            case CONTAINED -> new Color(255, 220, 105, 255);
            case CORRECT -> new Color(127, 255, 125, 255);
            default -> new Color(0, 0, 0, 0);
        };
    }
}
