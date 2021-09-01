package nextstep.joanne.server.http;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Headers {
    private final Map<String, String> headers;

    public Headers() {
        headers = new LinkedHashMap<>();
    }

    public Headers(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean contains(String key) {
        return headers.containsKey(key);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public Map<String, String> headers() {
        return headers;
    }

    public void addHeader(String key, String value) {
        if (Objects.nonNull(headers)) {
            headers.put(key, value);
        }
    }

    public String getHeaders() {
        StringBuilder stringBuilder = new StringBuilder();
        headers.forEach((key, value) -> {
            stringBuilder.append(key).append(": ").append(value).append(" \r\n");
        });
        return stringBuilder.toString();
    }
}
