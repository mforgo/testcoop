package NewTest;
import javax.swing.*;
import java.awt.*;

public class TreasureClientGUI extends JFrame {

    private static final int GRID_SIZE = 7;

    private JLabel triesLabel;
    private JLabel statusLabel;
    private JButton[][] cellButtons;

    private int tries = 0;

    public TreasureClientGUI() {
        initializeWindow();
        initializeComponents();
        setVisible(true);
    }

    private void initializeWindow() {
        setTitle("Treasure Hunting Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void initializeComponents() {
        //Horni panel
        JPanel topPanel = new JPanel(new GridLayout(2, 1));

        triesLabel = new JLabel("Tries: 0"); //pocet pokusu
        triesLabel.setFont(new Font("Arial", Font.BOLD, 18));

        statusLabel = new JLabel("Find the treasure!");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        topPanel.add(triesLabel);
        topPanel.add(statusLabel);

        add(topPanel, BorderLayout.NORTH);

        // Hraci plocha
        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 5, 5));
        cellButtons = new JButton[GRID_SIZE][GRID_SIZE];

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton("?");
                button.setFocusPainted(false);
                button.setFont(new Font("Arial", Font.BOLD, 20));
                final int r = row;
                final int c = col;
                button.addActionListener(e -> {
                    onCellClicked(r, c);
                });

                cellButtons[row][col] = button;
                gridPanel.add(button);
            }
        }

        add(gridPanel, BorderLayout.CENTER);
    }

    private void onCellClicked(int row, int col) {
        incrementTries();

        // tady lokalni ukazka
        cellButtons[row][col].setText("X");
        cellButtons[row][col].setEnabled(false);
    }

    public void incrementTries() {
        tries++;
        triesLabel.setText("Tries: " + tries);
    }


    public void revealEmptyCell(int row, int col) {
        cellButtons[row][col].setText(".");
        cellButtons[row][col].setEnabled(false);
    }

    public void revealTreasureCell(int row, int col) {
        cellButtons[row][col].setText("T");
        cellButtons[row][col].setEnabled(false);
    }

    public void resetBoard() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cellButtons[row][col].setText("?");
                cellButtons[row][col].setEnabled(true);
            }
        }
    }

    public void showTreasureFoundMessage(int row, int col) {
        JOptionPane.showMessageDialog(
                this,
                "Treasure found at (" + row + ", " + col + ")!\nNew round begins.",
                "Congratulations",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TreasureClientGUI::new);
    }
}
