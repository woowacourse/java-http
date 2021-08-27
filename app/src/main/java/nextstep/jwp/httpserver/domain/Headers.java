package nextstep.jwp.httpserver.domain;

import java.util.HashMap;
import java.util.Map;

public class Headers {
    private final Map<String, String> headers;

    public Headers(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
