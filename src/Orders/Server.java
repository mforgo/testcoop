package Orders;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private static final int PORT = 5001;
    private static final Map<String, Integer> inventory = Collections.synchronizedMap(new HashMap<String, Integer>());

    public static void main(String[] args) {
        System.out.println("Orders.Server spoustim na portu " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Orders.Client: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                // pro kazdeho clienta vlastni thread...
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Orders.Server konci...");
    }

    private static class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                while (true) {
                    Object o = in.readObject();
                    if (o instanceof Order ord) {
                        if (ord.type.equals(OrderType.PUT)){
                            if(inventory.containsKey(ord.itemName)) {
                                inventory.replace(ord.itemName, inventory.get(ord.itemName) + ord.qty);
                            } else {
                                inventory.put(ord.itemName, ord.qty);
                            }
                        } else {
                            if(inventory.containsKey(ord.itemName)) {
                                inventory.replace(ord.itemName, inventory.get(ord.itemName) - ord.qty);
                            } else {
                                // Chyba - není valid
                                // inventory.put(ord.itemName, ord.qty);
                            }
                        }
                    } else if (o instanceof StateRequest req) {
                        out.writeObject(new StateResponse(req.requestId, inventory));
                        out.flush();
                    } else {
                        // Chyba - není valid
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                // Orders.Client disconnect
                System.out.println("Orders.Client se odpojil: " + socket.getInetAddress() + ":" + socket.getPort());
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}