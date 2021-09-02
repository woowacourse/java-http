package nextstep.jwp.http.response;

import static java.util.stream.Collectors.toMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        Map<String, String> headers = lines.stream()
                .map(line -> line.split(": "))
                .filter(pair -> pair.length == 2)
                .collect(toMap(pair -> pair[0], pair -> pair[1]));
        return new ResponseHeaders(headers);
    }

    public void addAttribute(String key, String value) {
        headers.put(key, value);
    }

    public void addAttribute(String key, int value) {
        headers.put(key, String.valueOf(value));
    }

    public String getAttribute(String name) {
        return headers.get(name);
    }

    public List<String> asLines(String format) {
        return headers.keySet().stream()
                .map(key -> String.format(format, key, headers.get(key)))
                .collect(Collectors.toList());
    }
}
