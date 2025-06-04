package org.cis1200.TwoZeroFourEight;

/**
 * CIS 120 HW09 - TicTacToe Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */

import java.io.*;
import java.util.Arrays;

/**
 * This class is a model for TicTacToe.
 * 
 * This game adheres to a Model-View-Controller design framework.
 * This framework is very effective for turn-based games. We
 * STRONGLY recommend you review these lecture slides, starting at
 * slide 8, for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec36.pdf
 * 
 * This model is completely independent of the view and controller.
 * This is in keeping with the concept of modularity! We can play
 * the whole game from start to finish without ever drawing anything
 * on a screen or instantiating a Java Swing object.
 * 
 * Run this file to see the main method play a game of TicTacToe,
 * visualized with Strings printed to the console.
 */
public class TZFE {

    int[][] board;
    int score;
    private boolean gameOver;
    private int lastMove;



    public int[][] getBoard() {
        return board;
    }


    public static boolean isGameOver(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (j + 1 < board[0].length && board[i][j] == board[i][j + 1]) {
                    return false;
                }
                if (i + 1 < board.length && board[i][j] == board[i + 1][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public GameState takeSnapshot() {
        int[][] boardCopy = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            boardCopy[i] = Arrays.copyOf(board[i], board[i].length);
        }
        return new GameState(boardCopy, score);
    }

    // New method to restore from a GameState
    public void restoreSnapshot(GameState snapshot) {
        for (int i = 0; i < board.length; i++) {
            board[i] = Arrays.copyOf(snapshot.board[i], snapshot.board[i].length);
        }
        score = snapshot.score;
        gameOver = isGameOver(board);
    }

    // Add the GameState inner class to hold the state of the board and score
    public static class GameState {
        int[][] board;
        int score;

        GameState(int[][] board, int score) {
            this.board = board;
            this.score = score;
        }
    }

    /**
     * Constructor sets up game state.
     */
    public TZFE() {
        reset();
    }
    public TZFE(int[][] board) {
        this.board = board;
        score = 0;
        gameOver = false;
    }

    /**
     * playTurn allows players to play a turn. Returns true if the move is
     * successful and false if a player tries to play in a location that is
     * taken or after the game has ended. If the turn is successful and the game
     * has not ended, the player is changed. If the turn is unsuccessful or the
     * game has ended, the player is not changed.
     *
     * @param c column to play in
     * @param r row to play in
     * @return whether the turn was successful
     */
    // 1 is up, 2 is down, 3 is left, 4 is right
    public boolean playTurn(int dir, boolean b) {
        lastMove = dir;
        int[][] fin = new int[board.length][board[0].length];
        if (dir == 1) { // Up
            for (int i = 0; i < board[0].length; i++) {
                int[] col = new int[board.length];
                for (int j = 0; j < board.length; j++) {
                    col[j] = board[j][i];
                }
                col = reverseArray(smushRightAll(reverseArray(col)));
                for (int j = 0; j < board.length; j++) {
                    fin[j][i] = col[j];
                }
            }
        } else if (dir == 2) {
            for (int i = 0; i < board[0].length; i++) {
                int [] col = new int[board.length];
                for (int j = 0; j < board.length; j++) {
                    col[j] = board[j][i];
                }
                col = smushRightAll(col);
                for (int j = 0; j < board[0].length; j++) {
                    fin[j][i] = col[j];
                }

            }
        } else if (dir == 3) { // Left
            for (int i = 0; i < board.length; i++) {
                int[] row = new int[board[0].length];
                for (int j = 0; j < board[0].length; j++) {
                    row[j] = board[i][j];
                }
                row = reverseArray(smushRightAll(reverseArray(row)));
                for (int j = 0; j < board[0].length; j++) {
                    fin[i][j] = row[j];
                }
            }
        } else if (dir == 4) { // Right
            for (int i = 0; i < board.length; i++) {
                int[] row = new int[board[0].length];
                for (int j = 0; j < board[0].length; j++) {
                    row[j] = board[i][j];
                }
                row = smushRightAll(row);
                for (int j = 0; j < board[0].length; j++) {
                    fin[i][j] = row[j];
                }
            }
        }
        if (b) { //for testing to remove randomness
            for (int i = 0; i < fin.length; i++) {
                for (int j = 0; j < fin[0].length; j++) {
                    if (fin[i][j] == 0) {
                        int randomNum = (int) (Math.random() * 1001);
                        if (randomNum > 999) {
                            fin[i][j] = 32;
                        } else if (randomNum > 995) {
                            fin[i][j] = 16;
                        } else if (randomNum > 970) {
                            fin[i][j] = 8;
                        } else if (randomNum > 950) {
                            fin[i][j] = 4;
                        } else if (randomNum > 900) {
                            fin[i][j] = 2;
                        } else {
                            fin[i][j] = 0;
                        }
                    }
                }
            }
        }
        gameOver = isGameOver(fin);
        if (gameOver) {
            this.printGameState();
            System.out.println("Game Over");
        }
        board = fin;
        return true;
    }

    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(score + "\n");
            for (int[] row : board) {
                for (int value : row) {
                    writer.write(value + " ");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a game state from a file.
     */
    public void loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            score = Integer.parseInt(reader.readLine().trim());
            for (int i = 0; i < board.length; i++) {
                String[] values = reader.readLine().trim().split(" ");
                for (int j = 0; j < values.length; j++) {
                    board[i][j] = Integer.parseInt(values[j]);
                }
            }
            gameOver = isGameOver(board);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] reverseArray(int[] ints) {
        int[] reversed = new int[ints.length];
        for (int i = 0; i < ints.length; i++) {
            reversed[i] = ints[ints.length - 1 - i];
        }
        return reversed;
    }

    public int[] removeZeros(int[] og) {
        int [] fin = new int[og.length];
        int counter = 0;
        for (int i = og.length - 1; i >= 0 ; i--) {
            if (og[i] != 0) {
                fin[og.length - 1 - counter] = og[i];
                counter++;
            }
        }
        return fin;
    }

    public int[] smushRight(int[] og) {
        int[] fin = new int[og.length];
        for (int i = og.length - 1; i > 0; i--) {
            if (og[i] == og[i - 1] && og[i] != 0) {
                fin[i] = og[i] * 2;
                og[i - 1] = 0;
                score += fin[i];
            } else {
                fin[i] = og[i];
            }
        }
        fin[0] = og[0];
        return fin;
    }
    public int[] smushRightAll(int[] og) {
        return removeZeros(smushRight(removeZeros(og))) ;
    }


    /**
     * checkWinner checks whether the game has reached a win condition.
     * checkWinner only looks for horizontal wins.
     *
     * @return 0 if nobody has won yet, 1 if player 1 has won, and 2 if player 2
     *         has won, 3 if the game hits stalemate
     */
    public int checkWinner() {
        return 1;
    }

    /**
     * printGameState prints the current game state
     * for debugging.
     */
    public void printGameState() {
        System.out.println("\n\nScore: " + score + ":\n");
        System.out.println("\n\nLM: " + lastMove + ":\n");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j]);
                if (j < 3) {
                    System.out.print(" | ");
                }
            }
            if (i < 3) {
                System.out.println("\n---------");
            }
        }
    }

    /**
     * reset (re-)sets the game state to start a new game.
     */
    public void reset() {
        board = new int[4][4];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int randomNum = (int)(Math.random() * 1001);
                if (randomNum > 999) {
                    board[i][j] = 32;
                } else if (randomNum > 995) {
                    board[i][j] = 16;
                } else if (randomNum > 970) {
                    board[i][j] = 8;
                } else if (randomNum > 950) {
                    board[i][j] = 4;
                } else if (randomNum > 900) {
                    board[i][j] = 2;
                } else {
                    board[i][j] = 0;
                }

            }
        }
        score = 0;
        gameOver = false;
    }


    /**
     * getCell is a getter for the contents of the cell specified by the method
     * arguments.
     *
     * @param c column to retrieve
     * @param r row to retrieve
     * @return an integer denoting the contents of the corresponding cell on the
     *         game board. 0 = empty, 1 = Player 1, 2 = Player 2
     */
    public int getCell(int c, int r) {
        return board[r][c];
    }

    /**
     * This main method illustrates how the model is completely independent of
     * the view and controller. We can play the game from start to finish
     * without ever creating a Java Swing object.
     *
     * This is modularity in action, and modularity is the bedrock of the
     * Model-View-Controller design framework.
     *
     * Run this file to see the output of this method in your console.
     */
    public static void main(String[] args) {
        TZFE t = new TZFE();

        t.printGameState();
        System.out.println("");

        for (int i = 0; i < 1000; i++) {
            t.playTurn((int)(Math.random() * 5), true);
            t.printGameState();
        }
        t.playTurn(1, true);
        t.printGameState();
        t.playTurn(2, true);
        t.printGameState();
        t.playTurn(3, true);
        t.printGameState();
    }
}
