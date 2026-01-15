package remer.minesweeper;

import basicneuralnetwork.NeuralNetwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NeuralNetworkTest
{
    private NeuralNetwork network;
    private Random random;

    public NeuralNetworkTest(String filename) throws IOException
    {
        // load the trained network from file
        this.network = NeuralNetwork.readFromFile(filename);
        this.random = new Random();

        System.out.println("Neural network loaded from: " + filename);
    }

    // make a random move on the board
    private void makeRandomMove(MinesweeperModel model)
    {
        List<int[]> availableCells = new ArrayList<>();

        // find all unrevealed and unflagged cells
        for (int row = 0; row < model.getRows(); row++)
        {
            for (int col = 0; col < model.getCols(); col++)
            {
                Cell cell = model.getCell(row, col);
                if (!cell.isRevealed() && !cell.isFlagged())
                {
                    availableCells.add(new int[]{row, col});
                }
            }
        }

        // reveal a random cell
        if (!availableCells.isEmpty())
        {
            int[] cell = availableCells.get(random.nextInt(availableCells.size()));
            model.revealCell(cell[0], cell[1]);
        }
    }

    // count how many flags are on the board
    private int countFlags(MinesweeperModel model)
    {
        int count = 0;
        for (int row = 0; row < model.getRows(); row++)
        {
            for (int col = 0; col < model.getCols(); col++)
            {
                if (model.getCell(row, col).isFlagged())
                {
                    count++;
                }
            }
        }
        return count;
    }

    // play one game using the neural network
    private boolean playOneGame()
    {
        // create a new minesweeper game
        MinesweeperModel game = new MinesweeperModel(5, 5, 3);

        // make a random move to start
        makeRandomMove(game);

        int turnCount = 0;

        // keep playing until the game ends
        while (!game.isGameOver())
        {
            turnCount++;
            if (turnCount > 100)
            {
                break;
            }

            // get the current board state as input
            double[] input = game.toInput();

            // ask the network where it thinks the bombs are
            double[] output = network.guess(input);

            // count flags before placing new ones
            final int flagsBefore = countFlags(game);

            // flag cells where the network is confident there's a bomb
            for (int i = 0; i < output.length; i++)
            {
                if (output[i] >= 0.6)
                {
                    // convert 1D index to 2D coordinates
                    int row = i / 5;  // 5x5 board
                    int col = i % 5;

                    Cell cell = game.getCell(row, col);

                    // only flag if not already revealed and not already flagged
                    if (cell != null && !cell.isRevealed() && !cell.isFlagged())
                    {
                        game.toggleFlag(row, col);
                    }
                }
            }

            // count flags after placing new ones
            int flagsAfter = countFlags(game);

            // if we flagged something, auto-reveal safe cells, otherwise pick randomly
            if (flagsAfter > flagsBefore)
            {
                // a flag was added, so auto-reveal
                game.autoReveal();
            } else
            {
                // no flag was added, reveal a random cell
                makeRandomMove(game);
            }
        }

        // return true if game was won, false if lost
        return game.isGameWon();
    }

    // test the network by playing a bunch of games
    public void test()
    {
        System.out.println("\nStarting testing for 1,000 games...\n");

        int gamesWon = 0;
        int gamesLost = 0;

        for (int gameNum = 1; gameNum <= 1000; gameNum++)
        {
            boolean won = playOneGame();

            if (won)
            {
                gamesWon++;
            } else
            {
                gamesLost++;
            }

            // print progress every 100 games
            if (gameNum % 100 == 0)
            {
                double winRate = (double) gamesWon / gameNum * 100.0;
                System.out.printf("Game %,d / 1,000 - Win Rate: %.2f%% (%,d wins, %,d losses)\n",
                        gameNum, winRate, gamesWon, gamesLost);
            }
        }

        // print the final results
        System.out.println("\n=== Testing Complete ===");
        System.out.printf("Total Games: 1,000\n");
        System.out.printf("Wins: %,d\n", gamesWon);
        System.out.printf("Losses: %,d\n", gamesLost);
        System.out.printf("Win Rate: %.2f%%\n", (double) gamesWon / 1000 * 100.0);

        // hopefully we're winning more than half the time
        if ((double) gamesWon / 1000 * 100.0 > 50.0)
        {
            System.out.println("\n✓ SUCCESS! Win rate is > 50%");
        } else
        {
            System.out.println("\n✗ Win rate is <= 50%. More training may be needed.");
        }
    }

    public static void main(String[] args)
    {
        try
        {
            NeuralNetworkTest tester = new NeuralNetworkTest("minesweeper_model.nn");
            tester.test();
        } catch (IOException e)
        {
            System.err.println("Error loading neural network: " + e.getMessage());
            System.err.println("Make sure you run NeuralNetworkTeach first to create the network file.");
            e.printStackTrace();
        }
    }
}