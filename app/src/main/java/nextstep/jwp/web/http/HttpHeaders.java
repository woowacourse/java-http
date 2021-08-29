package nextstep.jwp.web.http;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.web.http.request.HttpRequestHeaderValues;

public class HttpHeaders {

    private final Map<String, HttpRequestHeaderValues> headers;

    public HttpHeaders() {
        this(new LinkedHashMap<>());
    }

    public HttpHeaders(
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

    public void addAll(HttpHeaders headers) {
        headers.addAll(headers);
    }

    public void set(String key, String... value) {
        headers.put(key, new HttpRequestHeaderValues(value));
    }

    public Map<String, HttpRequestHeaderValues> map() {
        return headers;
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
        return headers.getOrDefault(key, new HttpRequestHeaderValues());
    }
}
