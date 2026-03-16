package NewTest;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class TreasureClientGUI extends JFrame {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5555;
    private static final int GRID_SIZE = 7;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

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
                    onCellClicked(r,c);
                    sendReserve(r,c);
                });

                cellButtons[row][col] = button;
                gridPanel.add(button);
            }
        }

        add(gridPanel, BorderLayout.CENTER);
        connect();
    }

    private void connect() {
        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            //todo - novy thread a zacni poslouchat
            new Thread(this::listenLoop, "server-listener").start();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Could not connect to server at " + HOST + ":" + PORT + "\n" + ex.getMessage(),
                    "Connection error",
                    JOptionPane.ERROR_MESSAGE);

        }
    }

    private void listenLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();

                System.out.println(line);

                if (line.startsWith("REVEAL")) {
                    //todo
                    String[] parts = line.split("\\|");
                    if (parts.length == 4) {
                        String command = parts[1];
                        int r = Integer.parseInt(parts[2]);
                        int c = Integer.parseInt(parts[3]);
                        if (command.equalsIgnoreCase("EMPTY")){

                            SwingUtilities.invokeLater(() -> {
                                revealEmptyCell(r,c);
                            });

                        } else if (command.equalsIgnoreCase("TREASURE")){

                            SwingUtilities.invokeLater(() -> {
                                revealTreasureCell(r, c);
                            });

                        }
                    }
                } else if (line.startsWith("ERROR")) {
                    //todo
                    JOptionPane.showMessageDialog(null, "Nekdo uz diggnul", "Input error", JOptionPane.ERROR_MESSAGE);
                } else if (line.startsWith("RESERVED")) {

                    String[] parts = line.split("\\|");

                    if (parts.length == 3) {
                        int r = Integer.parseInt(parts[1]);
                        int c = Integer.parseInt(parts[2]);
                    SwingUtilities.invokeLater(() -> {
                        markReserved(r, c);
                    });
                    }
                }
            }
        } catch (IOException | NumberFormatException ex) {
            SwingUtilities.invokeLater(() -> {

                JOptionPane.showMessageDialog(null,
                        "Disconnected from server.",
                        "Disconnected",
                        JOptionPane.WARNING_MESSAGE);
            });
        } finally {
            closeQuietly();
        }
    }

    private void sendReserve(int r, int c) {
        if (out == null) return;

        out.println("DIG|" + r + "|" + c);
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
        incrementTries();
    }

    public void revealTreasureCell(int row, int col) {
        cellButtons[row][col].setText("T");
        cellButtons[row][col].setEnabled(false);
        showTreasureFoundMessage(row, col);
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
        resetBoard();
    }

    private void markReserved(int r, int c) {
        if (r < 0 || r >= GRID_SIZE || c < 0 || c >= GRID_SIZE) return;
        onCellClicked(r, c);
    }

    private void closeQuietly() {
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TreasureClientGUI::new);
    }
}
