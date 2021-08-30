package nextstep.jwp.http.response;

import java.util.*;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private final Map<String, String> headers;

    public ResponseHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders of(List<String> lines) {
        Map<String, String> headers = new HashMap<>();

        for (String line : lines) {
            String[] pair = line.split(": ");
            if (pair.length != 2) {
                break;
            }
            headers.put(pair[0], pair[1]);
        }
        return new ResponseHeaders(headers);
    }

    public void addAttribute(String key, String value) {
        headers.put(key, value);
    }

    public void addAttribute(String key, int value) {
        headers.put(key, String.valueOf(value));
    }

    public List<String> asLines(String format) {
        return headers.keySet().stream()
                .map(key -> String.format(format, key, headers.get(key)))
                .collect(Collectors.toList());
    }
}
