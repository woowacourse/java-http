package nextstep.jwp.http.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private final Map<String, String> headers;

    public ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public ResponseHeaders() {
        this(new HashMap<>());
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
    }

    public String asString() {
        return headers.entrySet().stream()
            .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining("\r\n"));
    }
}
