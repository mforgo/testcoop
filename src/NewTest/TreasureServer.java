package NewTest;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TreasureServer {
    private static final int PORT = 5555;
    private static final int SIZE = 7;

    private final boolean[][] reserved = new boolean[SIZE][SIZE];
    private final Set<PrintWriter> clientOutputs = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) throws IOException {
        new TreasureServer().start();
    }

    private void start() throws IOException {
        System.out.println("TreasureServer listening on port " + PORT);
        try (ServerSocket ss = new ServerSocket(PORT)) {
            while (true) {
                Socket s = ss.accept();
                System.out.println("Client connected: " + s.getRemoteSocketAddress());
                new Thread(() -> handleClient(s), "client-" + s.getPort()).start();
            }
        }
    }

    private void handleClient(Socket socket) {
        PrintWriter out = null;

        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            out = pw;
            clientOutputs.add(out);

            sendFullState(out);

            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("DIG")) {
                    String[] parts = line.split("\\|");
                    if (parts.length != 3) {
                        out.println("ERROR|BAD-FORMAT|-1|-1|");
                        continue;
                    }
                    int r = parseInt(parts[1]);
                    int c = parseInt(parts[2]);

                    boolean success = reserveCell(r, c);
                    if (success) {
                        broadcast("REVEAL|" + r + "|" + c);
                    } else {
                        out.println("ERROR|TAKEN|-1|-1|");
                    }
                } else {
                    out.println("ERROR|UNKNOW-COMMAND|-1|-1|");
                }
            }

        } catch (IOException e) {
            System.out.println("Client IO error: " + e.getMessage());
        } finally {
            if (out != null) clientOutputs.remove(out);
            System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
        }
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return -1; }
    }

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < SIZE && c >= 0 && c < SIZE;
    }

    private boolean reserveCell(int r, int c) {
        synchronized (reserved) {
            if (reserved[r][c]) return false;
            reserved[r][c] = true;
            return true;
        }
    }

    private void sendFullState(PrintWriter out) {
        synchronized (reserved) {
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    if (reserved[r][c]) {
                        out.println("RESERVED " + r + " " + c);
                    }
                }
            }
        }
    }

    private void broadcast(String msg) {
        synchronized (clientOutputs) {
            for (PrintWriter out : clientOutputs) {
                out.println(msg);
            }
        }
    }
}
