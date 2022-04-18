package com.fabii.wordle;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Tile {

    private char character;
    private Status status;

    public Tile(char character, Status status) {
        this.character = character;
        this.status = status;
    }

    public Tile() {
        this.character = ' ';
        this.status = Status.NOT_EVALUATED;
    }

    public char getCharacter() {
        return character;
    }

    public Status getStatus() {
        return status;
    }

    public void setCharacter(char character) {
        this.character = Character.toLowerCase(character);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void draw(Graphics g, int posX, int posY, Color color) {
        Color bg = Status.toColor(status);
        g.setColor(bg);
        g.fillRect(
            posX + Wordle.TILE_PADDING,
            posY + Wordle.TILE_PADDING,
            Wordle.TILE_SIZE - Wordle.TILE_PADDING * 2,
            Wordle.TILE_SIZE - Wordle.TILE_PADDING * 2
        );

        if (status == Status.NOT_EVALUATED) {
            g.setColor(color);
            g.drawRect(
                posX + Wordle.TILE_PADDING,
                posY + Wordle.TILE_PADDING,
                Wordle.TILE_SIZE - Wordle.TILE_PADDING * 2,
                Wordle.TILE_SIZE - Wordle.TILE_PADDING * 2
            );
        }

        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File("src/font.ttf"));
            g.setFont(font.deriveFont(30.0f));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        g.setColor(Wordle.colorFit(bg));
        g.drawString(
            String.valueOf(getCharacter()),
            posX + Wordle.TILE_SIZE / 2 - Wordle.TILE_PADDING,
            posY + Wordle.TILE_SIZE / 2 + Wordle.TILE_PADDING * 2
        );
    }
}
