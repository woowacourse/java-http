package nextstep.jwp.http.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    private final Map<String, String> headers;

    private HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders of() {
        return new HttpHeaders(new LinkedHashMap<>());
    }

    public static HttpHeaders of(Map<String, String> headers) {
        return new HttpHeaders(headers);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public boolean containsKey(String key) {
        return headers.containsKey(key);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public String convertToLines() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
