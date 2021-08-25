package nextstep.jwp.http.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestHeaders {

    private final Map<String, HttpRequestHeaderValues> headers;

    public HttpRequestHeaders() {
        this(new HashMap<>());
    }

    public HttpRequestHeaders(
        Map<String, HttpRequestHeaderValues> headers
    ) {
        this.headers = headers;
    }

    public void add(String key, String value) {
        add(key, Collections.singletonList(value));
    }

    public void add(String key, List<String> values) {
        addValueIfKeyPresent(key, values);
        addValueIfKeyAbsent(key, values);
    }

    private void addValueIfKeyPresent(String key, List<String> values) {
        headers.computeIfPresent(key,
            (k, v) -> v.add(values)
        );
    }

    private HttpRequestHeaderValues addValueIfKeyAbsent(String key, List<String> values) {
        return headers.computeIfAbsent(key,
            v -> new HttpRequestHeaderValues(values));
    }

    public String toValuesString(String key) {
        return headers.get(key).toValuesString();
    }

    public HttpRequestHeaderValues get(String key) {
        return headers.get(key);
    }
}
