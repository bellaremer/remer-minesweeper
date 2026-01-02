package remer.minesweeper;

import java.util.Random;

public class MinesweeperModel
{
    private Cell[][] board;
    private int rows;
    private int cols;
    private int numBombs;
    private boolean gameOver;
    private boolean gameWon;
    private boolean firstMove;
    private final Random random = new Random();

    // constructor
    public MinesweeperModel(int rows, int cols, int numBombs)
    {
        this.rows = rows;
        this.cols = cols;
        this.numBombs = numBombs;
        this.gameOver = false;
        this.gameWon = false;
        this.firstMove = true;
        this.board = new Cell[rows][cols];

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
                board[i][j] = new Cell();
            }
        }
    }

    private void placeBombs(int firstClickRow, int firstClickCol)
    {
        int bombsPlaced = 0;

        while (bombsPlaced < numBombs)
        {
            // generate random row and column
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);

            // only place bomb if this cell doesn't already  have one
            if (!board[row][col].isBomb() && !(row == firstClickRow && col == firstClickCol))
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
        // if it's the first move, place the bombs
        if (firstMove)
        {
            placeBombs(row, col);
            calculateAdjacentBombs();
            firstMove = false;
        }

        // check if game is already over or cell is invalid
        if (gameOver || !isValidCell(row, col))
        {
            return;
        }

        Cell cell = board[row][col];

        // don't reveal if already revealed or flagged
        if (cell.isRevealed() || cell.isFlagged())
        {
            return;
        }

        // reveal the cell
        cell.setRevealed(true);

        // if its a bomb, game over
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
                Cell cell = board[i][j];

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

        Cell cell = board[row][col];

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
        firstMove = true;
        board = new Cell[rows][cols];
        initializeBoard();
    }

    public Cell getCell(int row, int col)
    {
        if (row >= 0 && row < rows && col >= 0 && col < cols)
        {
            return board[row][col];
        }
        return null;
    }

}
