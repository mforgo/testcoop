package NewTest;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class GridClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5555;
    private static final int SIZE = 10;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final JLabel[][] cells = new JLabel[SIZE][SIZE];

    private final JTextField rowField = new JTextField(2);
    private final JTextField colField = new JTextField(2);
    private final JButton reserveButton = new JButton("Reserve");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GridClient().startUI());
    }

    private void startUI() {
        JFrame frame = new JFrame("10x10 Reservation Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel grid = new JPanel(new GridLayout(SIZE, SIZE, 2, 2));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                JLabel b = new JLabel();
                b.setPreferredSize(new Dimension(40, 40));

                // to u tlacitka zobrazi, jake ma souradnice
                b.setText(r + "," + c);

                cells[r][c] = b;
                grid.add(b);
            }
        }

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Row (0-9):"));
        controls.add(rowField);
        controls.add(new JLabel("Col (0-9):"));
        controls.add(colField);
        controls.add(reserveButton);

        reserveButton.addActionListener(e -> {
            //todo - poslat tlacitku instrukci
           sendReserve(parseField(rowField.getText()), parseField(colField.getText()));
        });

        frame.setLayout(new BorderLayout());
        frame.add(controls, BorderLayout.NORTH);
        frame.add(grid, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        connect();
    }

    private int parseField(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return -1; }
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
            setControlsEnabled(false);
        }
    }

    private void listenLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("RESERVED")) {
                    //todo
                    String[] parts = line.split("\\s+");
                    if (parts.length == 3) {
                        int r = Integer.parseInt(parts[1]);
                        int c = Integer.parseInt(parts[2]);
                        SwingUtilities.invokeLater(() -> {
                            markReserved(r,c);
                        });
                    }
                } else if (line.startsWith("ERROR")) {
                    //todo
                    JOptionPane.showMessageDialog(null, "Souradnice rezervovana", "Input error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IOException | NumberFormatException ex) {
            SwingUtilities.invokeLater(() -> {
                setControlsEnabled(false);
                JOptionPane.showMessageDialog(null,
                        "Disconnected from server.",
                        "Disconnected",
                        JOptionPane.WARNING_MESSAGE);
            });
        } finally {
            closeQuietly();
        }
    }

    private boolean sendReserve(int r, int c) {
        if (out == null) return false;

        //overte, ze souradnice jsou ok
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) {
            JOptionPane.showMessageDialog(null, "Souradnice musi byt 0..9", "Input error", JOptionPane.ERROR_MESSAGE);
            return false;
        }else {
            out.println("RESERVE " + r + " " + c);
            return true;
        }

    }

    private void markReserved(int r, int c) {
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) return;

        JLabel b = cells[r][c];
        b.setText("X"); // Obsadi misto
    }

    private void setControlsEnabled(boolean enabled) {
        //pozapina...
        reserveButton.setEnabled(enabled);
        rowField.setEnabled(enabled);
        colField.setEnabled(enabled);
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c].setEnabled(enabled && cells[r][c].isEnabled());
            }
        }
    }

    private void closeQuietly() {
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }
}
