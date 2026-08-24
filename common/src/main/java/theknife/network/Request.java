package theknife.network;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private RequestType type;
    private Map<String, Object> data;

    public Request(RequestType type) {
        this.type = type;
        this.data = new HashMap<>();
    }

    public Request(RequestType type, Map<String, Object> data) {
        this.type = type;
        this.data = data;
    }

    public RequestType getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void addData(String key, Object value) {
        data.put(key, value);
    }

    public Object getData(String key) {
        return data.get(key);
    }
}