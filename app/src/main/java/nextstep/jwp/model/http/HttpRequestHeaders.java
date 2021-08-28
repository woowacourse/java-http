package nextstep.jwp.model.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeaders {
    private final Map<String, String> headers = new HashMap<>();

    public void add(String name, String value) {
        headers.put(name, value);
    }

    public String get(String name) {
        return headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
