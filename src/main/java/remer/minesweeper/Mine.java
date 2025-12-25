package remer.minesweeper;

public class Mine
{
    private boolean isBomb;
    private int adjacentBombs;
    private boolean isRevealed;
    private boolean isFlagged;

    // constructor
    public Mine()
    {
        this.isBomb = false;
        this.adjacentBombs = 0;
        this.isRevealed = false;
        this.isFlagged = false;
    }

    // getters and setters
    public boolean isBomb()
    {
        return isBomb;
    }

    public void setBomb(boolean bomb)
    {
        isBomb = bomb;
    }

    public int getAdjacentBombs()
    {
        return adjacentBombs;
    }

    public void setAdjacentBombs(int adjacentBombs)
    {
        this.adjacentBombs = adjacentBombs;
    }

    public boolean isRevealed()
    {
        return isRevealed;
    }

    public void setRevealed(boolean revealed)
    {
        isRevealed = revealed;
    }

    public boolean isFlagged()
    {
        return isFlagged;
    }

    public void setFlagged(boolean flagged)
    {
        isFlagged = flagged;
    }
}
