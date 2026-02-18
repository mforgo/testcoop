import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 5001);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Scanner sc = new Scanner(System.in);
        ) {


            while (true) {

                System.out.println("order, state or exit: ");
                String os = sc.nextLine();

                if (os.equalsIgnoreCase("order")) {
                    try {
                        System.out.println("Type: ");
                        OrderType type = OrderType.valueOf(sc.nextLine());
                        System.out.println("Item: ");
                        String item = sc.nextLine();
                        System.out.println("qty: ");
                        int qty = sc.nextInt();

                        out.writeObject(new Order(type, item, qty));
                        out.flush();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }


                } else if (os.equalsIgnoreCase("state")) {
                    try {

                        System.out.println("Type a request ID");
                        int id = sc.nextInt();

                        out.writeObject(new StateRequest(id));
                        out.flush();

                        StateResponse res = (StateResponse) in.readObject();

                        System.out.println(res.toString());

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }


                } else if (os.equalsIgnoreCase("exit")) {

                    System.out.println("bye");
                    socket.close();

                } else {

                    System.out.println("please enter valid input!");

                }

                sc.nextLine();

            }



        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
