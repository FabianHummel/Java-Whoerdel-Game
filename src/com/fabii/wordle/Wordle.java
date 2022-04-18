package com.fabii.wordle;

import com.fabii.Frame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wordle {

    public static final int WORD_LENGTH = 5;
    public static final int ATTEMPTS = 6;

    public static final int TILE_SIZE = 50;
    public static final int TILE_PADDING = 5;

    private static final String AVAILABLE_CHARS = "abcdefghijklmnopqrstuvwxyz";

    public Frame frame;
    private String word;
    private final Tile[][] wordle = new Tile[ATTEMPTS][WORD_LENGTH];
    private int attempt = 0;
    private int index = 0;
    private boolean givenUp = false;

    public void draw(Graphics g) {
        int posY = (int) ((double) frame.getHeight() * 0.2);
        for (int i = 0; i < ATTEMPTS; i++) {
            Color color = new Color(255, 255, 255, 64);

            int posX = frame.getWidth() / 2 - (WORD_LENGTH * TILE_SIZE) / 2;
            for (int j = 0; j < WORD_LENGTH; j++) {
                if (i == attempt) {
                    color = setAlpha(
                        color, calcAlpha(j, index)
                    );
                }

                wordle[i][j].draw(g, posX, posY, color);
                posX += TILE_SIZE;
            }
            posY += TILE_SIZE;
        }
    }

    private Color setAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    private int getAlpha(Color color) {
        return color.getAlpha();
    }

    private int calcAlpha(int index, int cursor) {
        double num = -4.0 * Math.pow(
            index / 5.0 - cursor / 5.0, 2
        ) + 1;

        int alpha = (int) (num * 255.0);

        if (alpha < 128)
            alpha = 128;

        return alpha;
    }

    public void start() {
        attempt = 0;
        index = 0;

        word = randomWord();
        for (int i = 0; i < ATTEMPTS; i++) {
            for (int j = 0; j < WORD_LENGTH; j++) {
                wordle[i][j] = new Tile();
            }
        }
    }

    public void update() {

    }

    public void onKeyInput(KeyEvent e) {
        char key = Character.toLowerCase(
            e.getKeyChar()
        );

        // Backspace (⌫)
        if (e.getKeyCode() == 8) {
            if (index > 0) {
                wordle[attempt][--index].setCharacter(' ');
            }

        // Enter (⏎)
        } else if(e.getKeyCode() == 10) {
            evaluate(
                wordle[attempt]
            );

        // Keystroke
        } else if (contains(key)) {
            if (index < WORD_LENGTH) {
                wordle[attempt][index++].setCharacter(key);
            }
        }

        if (givenUp) {
            restart();
        }
    }

    private String toWord(Tile[] row) {
        StringBuilder sBuilder = new StringBuilder();
        for (Tile t : row) {
            sBuilder.append(t.getCharacter());
        } return sBuilder.toString();
    }

    private boolean checkWord(String word) {
        int length = word.length();
        try (BufferedReader bReader = new BufferedReader(new FileReader("src/dictionary.txt"))) {
            for (String line = bReader.readLine(); line != null; line = bReader.readLine())
                if (line.length() == length)
                    if (line.toLowerCase().equals(word))
                        return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private String randomWord() {
        List<String> available = new ArrayList<>();
        try (BufferedReader bReader = new BufferedReader(new FileReader("src/dictionary.txt"))) {
            for (String line = bReader.readLine(); line != null; line = bReader.readLine())
                if (line.length() == Wordle.WORD_LENGTH)
                    available.add(line.toLowerCase());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return available.get(
            new Random().nextInt(
                available.size()
            )
        );
    }

    private void evaluate(Tile[] row) {
        String word = toWord(row);

        // Check for complete word
        for (char c : word.toCharArray()) {
            if (c == ' ') {
                System.err.println("This word is not completed");
                return;
            }
        }

        // Check for existence
        if (!checkWord(word)) {
            System.err.println("This word is not inside the dictionary");
            return;
        }

        for (int i = 0; i < row.length; i++) {
            Tile tile = row[i];

            // Tile is in correct position
            if (tile.getCharacter() == this.word.charAt(i)) {
                tile.setStatus(Status.CORRECT);

            // Tile is inside the word, but not the correct position
            } else if (this.word.indexOf(tile.getCharacter()) != -1) {
                tile.setStatus(Status.CONTAINED);

            // Tile is not in the word
            } else {
                tile.setStatus(Status.EXCLUDED);
            }
        }

        attempt++;
        index = 0;
    }

    private boolean contains(char character) {
        for (char c : Wordle.AVAILABLE_CHARS.toCharArray()) {
            if (character == c) {
                return true;
            }
        }
        return false;
    }

    public static final Color aliceblue = new Color(232, 236, 255);
    public static final Color darkgray = new Color(30, 31, 33);
    public static Color colorFit(Color bg) {
        float[] vals = new float[3];
        Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), vals);
        return vals[2] < 0.5f ? aliceblue : darkgray;
    }

    public void giveUp() {
        System.err.println("Press any key to continue");
        for (int i = 0; i < WORD_LENGTH; i++) {
            wordle[attempt][i] = new Tile(
                word.charAt(i), Status.CORRECT
            );
        }

        attempt = 0;
        index = 0;
        givenUp = true;
    }

    public void restart() {
        givenUp = false;
        start();
    }

    public String getTitle() {
        return "Whördel";
    }
}
