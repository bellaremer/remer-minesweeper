package remer.minesweeper;

public class MinesweeperController
{
    private MinesweeperModel model;
    private MinesweeperView view;

    // constructor
    public MinesweeperController()
    {
        // create a 9x9 board with 10 bombs
        this.model = new MinesweeperModel(9, 9, 10);
    }

    // set the view
    public void setView(MinesweeperView view)
    {
        this.view = view;
    }

    // handle left click on cell to reveal it
    public void handleCellClick(int row, int col)
    {
        if (model.isGameOver())
        {
            return;
        }

        model.revealCell(row, col);
        view.updateBoard();

        // check game status and show message if needed
        if (model.isGameOver())
        {
            if (model.isGameWon())
            {
                view.showWinMessage();
            } else
            {
                view.showLoseMessage();
            }
        }
    }

    // handle right-click on a cell to flag it
    public void handleCellRightClick(int row, int col)
    {
        if (model.isGameOver())
        {
            return;
        }

        model.toggleFlag(row, col);
        view.updateBoard();
    }

    // start a new game
    public void newGame()
    {
        model.reset();
        view.updateBoard();
    }

    public void autoFlag()
    {
        if (model.isGameOver())
        {
            return;
        }

        model.autoFlag();
        view.updateBoard();
    }

    public void autoReveal()
    {
        if (model.isGameOver())
        {
            return;
        }

        model.autoReveal();
        view.updateBoard();

        // check the game status after auto-reveal
        if (model.isGameOver())
        {
            if (model.isGameWon())
            {
                view.showWinMessage();
            } else
            {
                view.showLoseMessage();
            }
        }
    }

    // get the model so the view can access cell information
    public MinesweeperModel getModel()
    {
        return model;
    }
}
