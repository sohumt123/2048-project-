package org.cis1200.TwoZeroFourEight;

/*
 * CIS 120 HW09 - TicTacToe Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */

import javax.swing.*;
import java.awt.*;
import java.io.File;


/**
 * This class sets up the top-level frame and widgets for the GUI.
 * 
 * This game adheres to a Model-View-Controller design framework. This
 * framework is very effective for turn-based games. We STRONGLY
 * recommend you review these lecture slides, starting at slide 8,
 * for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec37.pdf
 * 
 * In a Model-View-Controller framework, Game initializes the view,
 * implements a bit of controller functionality through the reset
 * button, and then instantiates a GameBoard. The GameBoard will
 * handle the rest of the game's view and controller functionality, and
 * it will instantiate a TicTacToe object to serve as the game's model.
 */
public class RunTwoZeroFourEight implements Runnable {
    private void showInstructions() {
        String instructionsText = "Welcome to 2048!\n\n" +
                "Objective:\n" +
                "Combine tiles with the same number by sliding them up, down, left, or right.\n\n" +
                "How to Play:\n" +
                "- Use the arrow keys or W, S, A, D keys to move the tiles.\n" +
                "- When two tiles with the same number collide," +
                "they merge into one with their sum.\n" +
                "- The goal is to create a tile with the number 2048.\n\n" +
                "Features:\n" +
                "- Undo your last move with the 'Undo' button.\n" +
                "- Save your current game state and load it" +
                " later using the 'Save' and 'Load' buttons.\n" +
                "- Reset the game at any time with the 'Reset' button.\n\n" +
                "Good luck and have fun!";

        JOptionPane.showMessageDialog(
                null,
                instructionsText,
                "Instructions",
                JOptionPane.INFORMATION_MESSAGE);

    }

    public void run() {


        final JFrame frame = new JFrame("2048");
        frame.setLocation(400, 400);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel scoreBoard = new JLabel("Score: 0");
        status_panel.add(scoreBoard);

        final JLabel lastMoveLabel = new JLabel("Last Move: None");
        status_panel.add(lastMoveLabel);

        // Game board
        final GameBoard board = new GameBoard(scoreBoard, lastMoveLabel);
        frame.add(board, BorderLayout.CENTER);

        // Reset button
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        // Undo button
        final JButton undo = new JButton("Undo");
        undo.addActionListener(e -> board.undo());
        control_panel.add(undo);

        // Note here that when we add an action listener to the reset button, we
        // define it as an anonymous inner class that is an instance of
        // ActionListener with its actionPerformed() method overridden. When the
        // button is pressed, actionPerformed() will be called.
        final JButton reset = new JButton("Reset");
        reset.addActionListener(e -> board.reset());
        control_panel.add(reset);

        // Save button
        final JButton save = new JButton("Save");
        save.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                board.saveToFile(file.getAbsolutePath());
            }
        });
        control_panel.add(save);

        // Load button
        final JButton load = new JButton("Load");
        load.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                board.loadFromFile(file.getAbsolutePath());
            }
        });
        control_panel.add(load);



        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);



        final JButton instructions = new JButton("Instructions");
        instructions.addActionListener(e -> showInstructions());
        status_panel.add(instructions);




        // Start the game
        board.reset();
    }
}