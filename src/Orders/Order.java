package Orders;

import java.io.Serializable;

public final class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    public int requestId = 0;
    public final OrderType type;
    public final String itemName;
    public final int qty;

    public Order( OrderType type, String itemName, int qty) {
        this.requestId++;
        this.type = type;
        this.itemName = itemName;
        this.qty = qty;
    }
}
