package com.fabii;

import com.fabii.wordle.Wordle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Frame extends JPanel implements Runnable {

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 700;

    private Thread thread;
    private boolean running;
    private static JFrame frame;

    private double deltaTime = 0;
    private static final int MAX_FPS = 60;
    private static final boolean UNLIMITED_FPS = false;

    private static final Wordle game = new Wordle();

    public static void main(String[] args) {
        frame = new JFrame();
        frame.add(new Frame());
        frame.setTitle(game.getTitle());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public Frame() {
        initialize();
    }

    private synchronized void startThread() {
        if(running)
            return;

        running = true;
        thread = new Thread(this);
        thread.start();
    }

    private synchronized void stopThread(){
        if(!running)
            return;

        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(1);
    }

    public void initialize() {
        this.setPreferredSize(new Dimension(
            WIDTH, HEIGHT
        ));
        this.setBackground(new Color(
            30, 31, 33
        ));
        this.setFocusable(true);
        this.startThread();

        JButton surrender = new JButton("Give up");
        surrender.addActionListener(e -> game.giveUp());
        surrender.setFocusable(false);
        this.add(surrender);

        JButton restart = new JButton("New Game");
        restart.addActionListener(e -> game.restart());
        restart.setFocusable(false);
        this.add(restart);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                game.onKeyInput(e);
            }
        });

        game.frame = this;
    }

    @Override
    public void run() {

        double skipAmount = 1.0 / MAX_FPS;

        int frameCount = 0;
        double timePassed = 0;

        long prev_frame_tick;
        long curr_frame_tick = System.nanoTime();

        game.start();

        while(running) {
            prev_frame_tick = curr_frame_tick;
            curr_frame_tick = System.nanoTime();

            double skipPassed = (curr_frame_tick - prev_frame_tick) / 1000000000.0;
            timePassed += skipPassed;
            deltaTime += skipPassed;

            if (UNLIMITED_FPS || deltaTime() >= skipAmount) {
                game.update();
                repaint();

                frameCount++;
                deltaTime = 0;

                if (timePassed > 1) {
                    frame.setTitle(game.getTitle() + " | FPS: " + frameCount);
                    timePassed = 0;
                    frameCount = 0;
                }
            }
        }

        stopThread();
    }

    public double deltaTime() {
        return deltaTime;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        game.draw(g);
    }
}