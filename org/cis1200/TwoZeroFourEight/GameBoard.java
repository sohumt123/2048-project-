package org.cis1200.TwoZeroFourEight;

/*
 * CIS 120 HW09 - TicTacToe Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Stack;

/**
 * This class instantiates a TicTacToe object, which is the model for the game.
 * As the user clicks the game board, the model is updated. Whenever the model
 * is updated, the game board repaints itself and updates its status JLabel to
 * reflect the current state of the model.
 * 
 * This game adheres to a Model-View-Controller design framework. This
 * framework is very effective for turn-based games. We STRONGLY
 * recommend you review these lecture slides, starting at slide 8,
 * for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec37.pdf
 * 
 * In a Model-View-Controller framework, GameBoard stores the model as a field
 * and acts as both the controller (with a MouseListener) and the view (with
 * its paintComponent method and the status JLabel).
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {

    private TZFE tzfe; // model for the game
    private JLabel scoreBoard;
    private JLabel lastMoveLabel;
    private Stack<TZFE.GameState> history;

    private int score;
    private int lastMove;

    // Game constants
    public static final int BOARD_WIDTH = 400;
    public static final int BOARD_HEIGHT = 400;

    /**
     * Initializes the game board.
     */
    public GameBoard(JLabel scoreBoardInit, JLabel lastMoveLabelInit) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        tzfe = new TZFE();
        scoreBoard = scoreBoardInit;
        lastMoveLabel = lastMoveLabelInit; // initializes the status JLabel
        history = new Stack<>();

        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int dir = 0;
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                    dir = 1;
                    lastMoveLabel.setText("Last Move: Up");
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
                    dir = 2;
                    lastMoveLabel.setText("Last Move: Down");
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
                    dir = 3;
                    lastMoveLabel.setText("Last Move: Left");
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
                    dir = 4;
                    lastMoveLabel.setText("Last Move: Right");
                }

                if (dir != 0) {
                    history.push(tzfe.takeSnapshot());

                    tzfe.playTurn(dir, true);
                    updateStatus(); // Updates the status JLabel
                    repaint(); // Repaints the game board
                    if (tzfe.isGameOver(tzfe.board)) {
                        handleGameOver();
                    }
                }
            }
        });
    }

    public void undo() {
        if (!history.isEmpty()) {
            TZFE.GameState lastState = history.pop();
            tzfe.restoreSnapshot(lastState);
            updateStatus();
            repaint();
            requestFocusInWindow();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No moves to undo!",
                    "Undo Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * (Re-)sets the game to its initial state.
     */
    public void reset() {
        tzfe.reset();
        history.clear();
        scoreBoard.setText("Score: " + score);
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateStatus() {
        scoreBoard.setText("Score: " + tzfe.score);
    }

    /**
     * Draws the game board.
     * 
     * There are many ways to draw a game board. This approach
     * will not be sufficient for most games, because it is not
     * modular. All of the logic for drawing the game board is
     * in this method, and it does not take advantage of helper
     * methods. Consider breaking up your paintComponent logic
     * into multiple methods or classes, like Mushroom of Doom.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draws board grid
        int unitWidth = BOARD_WIDTH / 4;
        int unitHeight = BOARD_HEIGHT / 4;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int x = j * unitWidth;
                int y = i * unitHeight;

                // Draw the numbers
                int value = tzfe.getCell(j, i);

                if (value == 0) {
                    g.setColor(Color.LIGHT_GRAY);
                } else {
                    g.setColor(getColorForValue(value));
                }
                g.fillRect(x, y, unitWidth, unitHeight);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, unitWidth, unitHeight);

                if (value != 0) {
                    g.setFont(new Font("SansSerif", Font.BOLD, 24));
                    g.setColor(Color.BLACK);
                    FontMetrics fm = g.getFontMetrics();
                    String text = String.valueOf(value);
                    int textWidth = fm.stringWidth(text);
                    int textHeight = fm.getHeight();
                    g.drawString(text, x + (unitWidth - textWidth) / 2,
                            y + (unitHeight + textHeight) / 2 - 5);
                }
            }
        }
    }

    private void handleGameOver() {
        int response = JOptionPane.showOptionDialog(
                this,
                "Game Over! Your score is: " + tzfe.score + ". Would you like to play again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Yes", "No"},
                "Yes"
        );

        // If the user chooses "Yes", reset the game
        if (response == JOptionPane.YES_OPTION) {
            reset();
        }
    }

    public void saveToFile(String filename) {
        tzfe.saveToFile(filename);
        requestFocusInWindow();
    }

    public void loadFromFile(String filename) {
        tzfe.loadFromFile(filename);
        updateStatus();
        repaint();
        requestFocusInWindow();
    }


    private Color getColorForValue(int value) {
        switch (value) {
            case 2:
                return new Color(0xEEE4DA);
            case 4:
                return new Color(0xEDE0C8);
            case 8:
                return new Color(0xF2B179);
            case 16:
                return new Color(0xF59563);
            case 32:
                return new Color(0xF67C5F);
            case 64:
                return new Color(0xF65E3B);
            case 128:
                return new Color(0xEDCF72);
            case 256:
                return new Color(0xEDCC61);
            case 512:
                return new Color(0xEDC850);
            case 1024:
                return new Color(0xEDC53F);
            case 2048:
                return new Color(0xEDC22E);
            default:
                return new Color(0x3C3A32);
        }
    }
    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}
