package Orders;

import java.io.Serializable;

public final class StateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public final int requestId;

    public StateRequest(int requestId) {
        this.requestId = requestId;
   }
}
