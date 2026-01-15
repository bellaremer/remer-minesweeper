package remer.minesweeper;

import basicneuralnetwork.NeuralNetwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NeuralNetworkTeach
{
    private NeuralNetwork network;
    private Random random;

    public NeuralNetworkTeach()
    {
        // create a new neural network
        // input layer size = rows x columns = 5 x 5 = 25
        // output layer size = rows x columns = 5 x 5 = 25
        // number of hidden layers = 1
        // size of hidden layers = 128
        this.network = new NeuralNetwork(25, 128, 25);
        this.random = new Random();

        System.out.println("Neural Network created:");
        System.out.println("  Input layer: 25 nodes");
        System.out.println("  Hidden layer: 128 nodes");
        System.out.println("  Output layer: 25 nodes");
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

    // play one game and train the network
    private boolean playOneGame()
    {
        // create minesweeper game (5x5 with 3 mines) - no UI
        MinesweeperModel original = new MinesweeperModel(5, 5, 3);

        // make an initial random move
        makeRandomMove(original);

        // loop until game won or game over
        while (!original.isGameOver())
        {
            // create a deep copy of minesweeper game
            MinesweeperModel copy = original.deepCopy();

            // count flags before auto-flagging
            final int flagsBefore = countFlags(copy);

            // auto-flag on the copy
            copy.autoFlag();

            // count flags after auto-flagging
            int flagsAfter = countFlags(copy);

            // get two arrays, original.toInput() and copy.toOutput()
            double[] input = original.toInput();
            double[] output = copy.toOutput();  // use bomb locations, not flag locations

            // train the network with this example
            network.train(input, output);

            // update original to be the copy
            original = copy;

            // if a flag was added, auto-reveal, otherwise reveal random cell
            if (flagsAfter > flagsBefore)
            {
                // a flag was added, so auto-reveal
                original.autoReveal();
            } else
            {
                // no flag was added, reveal a random cell
                makeRandomMove(original);
            }
        }

        // return true if game was won, false if lost
        return original.isGameWon();
    }

    // train the network by playing a million games
    public void teach()
    {
        System.out.println("\nStarting training for 1,000,000 games...\n");

        int gamesWon = 0;
        int gamesLost = 0;

        for (int gameNum = 1; gameNum <= 1000000; gameNum++)
        {
            boolean won = playOneGame();

            if (won)
            {
                gamesWon++;
            } else
            {
                gamesLost++;
            }

            // print progress every 10,000 games
            if (gameNum % 10000 == 0)
            {
                double winRate = (double) gamesWon / gameNum * 100.0;
                System.out.printf("Game %,d / 1,000,000 - Win Rate: %.2f%% (%,d wins, %,d losses)\n",
                        gameNum, winRate, gamesWon, gamesLost);
            }
        }

        // print final results
        System.out.println("\n=== Training Complete ===");
        System.out.printf("Total Games: 1,000,000\n");
        System.out.printf("Wins: %,d\n", gamesWon);
        System.out.printf("Losses: %,d\n", gamesLost);
        System.out.printf("Win Rate: %.2f%%\n", (double) gamesWon / 1000000 * 100.0);

        // save the trained network to a file
        try
        {
            network.writeToFile("minesweeper_model.nn");
            System.out.println("\nNeural network saved to: minesweeper_model.nn");
        } catch (IOException e)
        {
            System.err.println("Error saving neural network: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        NeuralNetworkTeach teacher = new NeuralNetworkTeach();
        teacher.teach();
    }
}