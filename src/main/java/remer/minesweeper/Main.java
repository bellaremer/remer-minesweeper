package remer.minesweeper;

public class Main {
    public static void main(String[] args)
    {
        MinesweeperController controller = new MinesweeperController();
        new MinesweeperView(controller);
    }
}