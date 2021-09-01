package nextstep.joanne.http.request;

import java.util.Map;

public class RequestHeaders2 {
    private final Map<String, String> headers;

    public RequestHeaders2(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean contains(String key) {
        return headers.containsKey(key);
    }

    public String get(String key) {
        return headers.get(key);
    }
}
