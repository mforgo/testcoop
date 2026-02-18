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



            while (true){

                System.out.println("order, state or exit: ");
                String os = sc.nextLine().strip();

                if (os.equalsIgnoreCase("order")){

                    System.out.println("Type: ");
                    OrderType type = OrderType.valueOf(sc.nextLine());
                    System.out.println("Item: ");
                    String item = sc.nextLine();
                    System.out.println("qty: ");
                    int qty = sc.nextInt();

                    out.writeObject(new Order(type, item, qty));
                    out.flush();


                } else if (os.equalsIgnoreCase("state")){

                    System.out.println("Type a request ID");
                    int id = sc.nextInt();

                    out.writeObject(new StateRequest(id));
                    out.flush();

                    StateResponse res = (StateResponse) in.readObject();

                    System.out.println(res.requestId + "; " + res.items.toString());

                }else if (os.equalsIgnoreCase("exit")) {

                    System.out.println("bye");
                    socket.close();

                }






            }


           /* System.out.println("Id: ");
            int id  = sc.nextInt();
            System.out.println("Type: ");
            OrderType type = OrderType.valueOf(sc.nextLine());
            System.out.println("Item: ");
            String item = sc.nextLine();
            System.out.println("qty: ");
            int qty = sc.nextInt();

            out.writeObject(new Order(id, type, item, qty));
            out.flush();*/




            //StateResponse res = (StateResponse) in.readObject();


        } catch (IOException | ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
    }
}
