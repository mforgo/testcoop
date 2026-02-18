import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public final class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    public final int requestId;
    public final OrderType type;
    public final String itemName;
    public final int qty;

    public Order(int requestId, OrderType type, String itemName, int qty) {
        this.requestId = requestId;
        this.type = type;
        this.itemName = itemName;
        this.qty = qty;
    }
}
