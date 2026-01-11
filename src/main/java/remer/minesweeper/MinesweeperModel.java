package remer.minesweeper;

import java.util.Random;

public class MinesweeperModel
{
    private Mine[][] board;
    private int rows;
    private int cols;
    private int numBombs;
    private boolean gameOver;
    private boolean gameWon;

    // constructor
    public MinesweeperModel(int rows, int cols, int numBombs)
    {
        this.rows = rows;
        this.cols = cols;
        this.numBombs = numBombs;
        this.gameOver = false;
        this.gameWon = false;
        this.board = new Mine[rows][cols];

        initializeBoard();
    }

    // Initialize the board with Mine objects
    private void initializeBoard()
    {
        // create all Mine objects
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                board[i][j] = new Mine();
            }
        }

        // randomly place the bombs
        placeBombs();

        // calculate adjacent bomb count
        calculateAdjacentBombs();
    }

    private void placeBombs()
    {
        Random rand = new Random();
        int bombsPlaced = 0;

        while (bombsPlaced < numBombs)
        {
            // generate random row and column
            int row = rand.nextInt(rows);
            int col = rand.nextInt(cols);

            // only place bomb if this cell doesn't already  have one
            if (!board[row][col].isBomb())
            {
                board[row][col].setBomb(true);
                bombsPlaced++;
            }
        }
    }

    private void calculateAdjacentBombs()
    {
        // go through every cell on the board
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                // only calculate for non-bombs cells
                if (!board[row][col].isBomb())
                {
                    int count = countAdjacentBombs(row, col);
                    board[row][col].setAdjacentBombs(count);
                }
            }
        }
    }

    private int countAdjacentBombs(int row, int col)
    {
        int count = 0;

        // check all 8 directions around the cell
        for (int i = -1; i <= 1; i++)
        {
            for (int j = -1; j <= 1; j++)
            {
                // skip the cell itself
                if (i == 0 && j == 0)
                {
                    continue;
                }

                int newRow = row + i;
                int newCol = col + j;

                // check if the adjacent cell is within bounds and is a bomb
                if (isValidCell(newRow, newCol) && board[newRow][newCol].isBomb())
                {
                    count++;
                }
            }
        }
        return count;
    }

    // helper method to check if a cell is withing the board boundries
    private boolean isValidCell(int row, int col)
    {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public void revealCell(int row, int col)
    {
        // check if game is already over or cell is invalid
        if (gameOver || !isValidCell(row, col))
        {
            return;
        }

        Mine cell = board[row][col];

        // don't reveal if already revealed or flagged
        if (cell.isRevealed() || cell.isFlagged())
        {
            return;
        }

        // reveal the cell
        cell.setRevealed(true);

        // if it's a bomb, game over
        if (cell.isBomb())
        {
            gameOver = true;
            revealAllBombs();
            return;
        }

        // if it's a 0, reveal all adjacent cells recursively
        if (cell.getAdjacentBombs() == 0)
        {
            revealAdjacentCells(row, col);
        }

        // check if player won
        checkWin();
    }

    private void revealAdjacentCells(int row, int col)
    {
        // check all 8 directions
        for (int i = -1; i <= 1; i++)
        {
            for (int j = -1; j <= 1; j++)
            {
                // skip the cell itself
                if (i == 0 && j == 0)
                {
                    continue;
                }

                int newRow = row + i;
                int newCol = col + j;

                // recursively reveal adjacent cells
                if (isValidCell(newRow, newCol))
                {
                    revealCell(newRow, newCol);
                }
            }
        }
    }

    private void revealAllBombs()
    {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (board[i][j].isBomb())
                {
                    board[i][j].setRevealed(true);
                }
            }
        }
    }

    private void checkWin()
    {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                Mine cell = board[i][j];

                // if any non-bomb cells is still hidden, game is not won
                if (!cell.isBomb() && !cell.isRevealed())
                {
                    return;
                }
            }
        }

        // all non-bomb cells are revealed, player wins :)
        gameOver = true;
        gameWon = true;
    }

    public void toggleFlag(int row, int col)
    {
        // can't flag if game is over or cell is invalid
        if (gameOver || !isValidCell(row, col))
        {
            return;
        }

        Mine cell = board[row][col];

        // can't flag an already revealed cell
        if (cell.isRevealed())
        {
            return;
        }

        // toggle the flag status
        cell.setFlagged(!cell.isFlagged());
    }

    public boolean isGameOver()
    {
        return gameOver;
    }

    public boolean isGameWon()
    {
        return gameWon;
    }

    public int getRows()
    {
        return rows;
    }

    public int getCols()
    {
        return cols;
    }

    public int getNumBombs()
    {
        return numBombs;
    }

    // reset the game
    public void reset()
    {
        gameOver = false;
        gameWon = false;
        board = new Mine[rows][cols];
        initializeBoard();
    }

    public Mine getCell(int row, int col)
    {
        if (row >= 0 && row < rows && col >= 0 && col < cols)
        {
            return board[row][col];
        }
        return null;
    }

    // auto-flag cells that we logically determine are bombs
    public void autoFlag()
    {
        // iterate through all cells
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                Mine cell = board[row][col];

                // skip if cell is not revealed, a bomb, flagged, or has 0 adjacent bombs
                if (!cell.isRevealed() || cell.isBomb() || cell.isFlagged() || cell.getAdjacentBombs() == 0)
                {
                    continue;
                }

                int hiddenCount = 0;
                int flaggedCount = 0;

                // check all 8 neighbors
                for (int i = -1; i <= 1; i++)
                {
                    for (int j = -1; j <= 1; j++)
                    {
                        // skip the cell itself
                        if (i == 0 && j == 0)
                        {
                            continue;
                        }

                        int newRow = row + i;
                        int newCol = col + j;

                        if (isValidCell(newRow, newCol))
                        {
                            Mine neighbor = board[newRow][newCol];

                            if (neighbor.isFlagged())
                            {
                                flaggedCount++;
                            } else if (!neighbor.isRevealed())
                            {
                                hiddenCount++;
                            }
                        }
                    }
                }

                // check if cell is "satisfied"
                // if flagged + hidden == number on cell, all hidden neighbors must be bombs
                if (flaggedCount + hiddenCount == cell.getAdjacentBombs())
                {
                    // flag all hidden neighbors
                    for (int i = -1; i <= 1; i++)
                    {
                        for (int j = -1; j <= 1; j++)
                        {
                            // skip the cell itself
                            if (i == 0 && j == 0)
                            {
                                continue;
                            }

                            int newRow = row + i;
                            int newCol = col + j;

                            if (isValidCell(newRow, newCol))
                            {
                                Mine neighbor = board[newRow][newCol];

                                // flag if its hidden
                                if (!neighbor.isRevealed() && !neighbor.isFlagged())
                                {
                                    neighbor.setFlagged(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // auto-reveal cells that we can logically determine are safe
    public void autoReveal()
    {
        // iterate through all the cells
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                Mine cell = board[row][col];

                // skip if the cell is hidden, a bomb, flagged, or has 0 adjacent bombs
                if (!cell.isRevealed() || cell.isBomb() || cell.isFlagged() || cell.getAdjacentBombs() == 0)
                {
                    continue;
                }

                int flaggedCount = 0;

                // check all 8 neighbors
                for (int i = -1; i <= 1; i++)
                {
                    for (int j = -1; j <= 1; j++)
                    {
                        // skip the cell itself
                        if (i == 0 && j == 0)
                        {
                            continue;
                        }

                        int newRow = row + i;
                        int newCol = col + j;

                        if (isValidCell(newRow, newCol))
                        {
                            Mine neighbor = board[newRow][newCol];

                            if (neighbor.isFlagged())
                            {
                                flaggedCount++;
                            }
                        }
                    }
                }

                // if flagged count equals the number on the cell,
                // all remaining hidden neighbors are safe
                if (flaggedCount == cell.getAdjacentBombs())
                {
                    // reveal all hidden neighbors
                    for (int i = -1; i <= 1; i++)
                    {
                        for (int j = -1; j <= 1; j++)
                        {
                            // skip the cell itself
                            if (i == 0 && j == 0)
                            {
                                continue;
                            }

                            int newRow = row + i;
                            int newCol = col + j;

                            if (isValidCell(newRow, newCol))
                            {
                                // reveal the cell
                                revealCell(newRow, newCol);
                            }
                        }
                    }
                }
            }
        }
    }

    // convert the board to a 1D array of doubles for neural network
    // size = rows * cols (for 9x9 board = 81 cells/elements)
    public double[] toInput()
    {
        double[] input = new double[rows * cols];
        int index = 0;

        // iterate through the board row by row
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                Mine cell = board[row][col];

                // if cell is flagged, value is 1.0
                if (cell.isFlagged())
                {
                    input[index] = 1.0;
                } else if (cell.isRevealed())
                {
                    // if cell is revealed, use chart based on adjacent bombs
                    int adjacentBombs = cell.getAdjacentBombs();
                    input[index] = (adjacentBombs + 1) * 0.1;
                }
                index++;
            }
        }
        return input;
    }

    // convert flag locations to a 1D array of doubles for neural network (where the flags are expected)
    // size = rows * cols (for 9x9 board = 81 cells/elements)
    public double[] toOutput()
    {
        double[] output = new double[rows * cols];
        int index = 0;

        // iterate through the board row by row
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                Mine cell = board[row][col];

                // if the cell has a flag, value is 1.0
                if (cell.isFlagged())
                {
                    output[index] = 1.0;
                }
                index++;
            }
        }
        return output;
    }

    // new method to get the actual locations for training - tells the network where the actual bombs are
    public double[] toBombOutput()
    {
        double[] output = new double[rows * cols];
        int index = 0;

        // iterate through the board row by row
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                Mine cell = board[row][col];

                // if the cell IS A BOMB (not just flagged), value is 1.0
                if (cell.isBomb())
                {
                    output[index] = 1.0;
                }
                index++;
            }
        }
        return output;
    }

    // create a deep copy of the MinesweeperModel
    public MinesweeperModel deepCopy()
    {
        MinesweeperModel copy = new MinesweeperModel(this.rows, this.cols, this.numBombs);

        // copy fields
        copy.gameOver = this.gameOver;
        copy.gameWon = this.gameWon;

        // deep copy the board - clone each Mine object
        copy.board = new Mine[rows][cols];
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                copy.board[row][col] = this.board[row][col].clone();
            }
        }
        return copy;
    }
}
