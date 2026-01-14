package remer.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MinesweeperView extends JFrame
{
    private MinesweeperController controller;
    private JButton[][] buttons;
    private JPanel boardPanel;
    private JButton resetButton;
    private JButton autoFlagButton;
    private JButton autoRevealButton;

    // constructor
    public MinesweeperView(MinesweeperController controller)
    {
        this.controller = controller;
        controller.setView(this);

        // set up the window
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // create the board and reset button
        createBoard();
        createResetButton();

        pack();
        setLocationRelativeTo(null); // center on the screen
        setVisible(true);
    }

    private void createBoard()
    {
        MinesweeperModel model = controller.getModel();
        int rows = model.getRows();
        int cols = model.getCols();

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(rows, cols));
        buttons = new JButton[rows][cols];

        // create a button for each cell
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(50, 50));
                button.setFont(new Font("Arial", Font.BOLD, 16));

                // store row and col for the click handler
                final int r = row;
                final int c = col;

                // add mouse listener for left and right click
                button.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mousePressed(MouseEvent e)
                    {
                        if (SwingUtilities.isLeftMouseButton(e))
                        {
                            controller.handleCellClick(r, c);
                        } else if (SwingUtilities.isRightMouseButton(e))
                        {
                            controller.handleCellRightClick(r, c);
                        }

                    }
                });

                buttons[row][col] = button;
                boardPanel.add(button);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
    }

    private void createResetButton()
    {
        resetButton = new JButton("New Game");
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.addActionListener(e -> controller.newGame());

        autoFlagButton = new JButton("Auto Flag");
        autoFlagButton.setFont(new Font("Arial", Font.BOLD, 14));
        autoFlagButton.addActionListener(e -> controller.autoFlag());

        autoRevealButton = new JButton("Auto Reveal");
        autoRevealButton.setFont(new Font("Arial", Font.BOLD, 14));
        autoRevealButton.addActionListener(e -> controller.autoReveal());

        JPanel topPanel = new JPanel();
        topPanel.add(resetButton);
        topPanel.add(autoFlagButton);
        topPanel.add(autoRevealButton);
        add(topPanel, BorderLayout.NORTH);
    }

    // update the board display
    public void updateBoard()
    {
        MinesweeperModel model = controller.getModel();

        for (int row = 0; row < model.getRows(); row++)
        {
            for (int col = 0; col < model.getCols(); col++)
            {
                Cell cell = model.getCell(row, col);
                JButton button = buttons[row][col];

                if (cell.isRevealed())
                {
                    button.setEnabled(false);

                    if (cell.isBomb())
                    {
                        button.setText("💣");
                        button.setBackground(Color.RED);
                    } else
                    {
                        int adjacent = cell.getAdjacentBombs();
                        if (adjacent > 0)
                        {
                            button.setText(String.valueOf(adjacent));
                            button.setForeground(getNumberColor(adjacent));
                        } else
                        {
                            button.setText("");
                        }
                        button.setBackground(Color.LIGHT_GRAY);
                    }
                } else if (cell.isFlagged())
                {
                    button.setText("🚩");
                    button.setBackground(null);
                } else
                {
                    button.setText("");
                    button.setEnabled(true);
                    button.setBackground(null);
                }
            }
        }
    }

    // get color for numbers based on how many adjacent bombs
    private Color getNumberColor(int num)
    {
        switch (num)
        {
            case 1: return Color.BLUE;
            case 2: return Color.GREEN;
            case 3: return Color.RED;
            case 4: return new Color(0, 0, 128); // Dark blue
            case 5: return new Color(128, 0, 0); // Dark red
            case 6: return Color.CYAN;
            case 7: return Color.BLACK;
            case 8: return Color.GRAY;
            default: return Color.BLACK;
        }
    }

    // show win message
    public void showWinMessage()
    {
        JOptionPane.showMessageDialog(this,
                "Congratulations! You win!",
                "Victory!!",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // show lose message
    public void showLoseMessage()
    {
        JOptionPane.showMessageDialog(this,
                "Game Over :( \n you hit a bomb!",
                "Defeat!",
                JOptionPane.ERROR_MESSAGE);
    }
}
