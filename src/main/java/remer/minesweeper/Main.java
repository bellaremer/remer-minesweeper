package remer.minesweeper;

public class Main
{
    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                // create the controller
                MinesweeperController controller = new MinesweeperController();

                // create the view
                new MinesweeperView(controller);

            }
        });
    }
}
