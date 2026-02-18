import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class StateResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    public final int requestId;
    public final Map<String, Integer> items; // itemName -> qty

    public StateResponse(int requestId, Map<String, Integer> items) {
        this.requestId = requestId;
        this.items = items;
    }

}
