package nextstep.jwp.webserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpHeaders(List<String> headerList) {
        this(parseHeaders(headerList));
    }

    public HttpHeaders() {
        this(new HashMap<>());
    }

    private static Map<String, String> parseHeaders(List<String> headerList) {
        Map<String, String> headers = new HashMap<>();
        for (String headerEntity : headerList) {
            putHeader(headers, headerEntity);
        }
        return headers;
    }

    private static void putHeader(Map<String, String> headers, String header) {
        int index = header.indexOf(":");
        String key = header.substring(0, index);
        String value = header.substring(index + 1).trim();
        headers.put(key, value);
    }

    public String get(String name) {
        return headers.get(name);
    }

    public boolean contains(String header) {
        return headers.containsKey(header);
    }

    public void set(String name, String value) {
        headers.put(name, value);
    }

    private String headerToString(String name) {
        return name + ": " + headers.get(name);
    }

    public String getString() {
        return headers.keySet().stream()
                .map(this::headerToString)
                .collect(Collectors.joining("\r\n"));
    }
}
