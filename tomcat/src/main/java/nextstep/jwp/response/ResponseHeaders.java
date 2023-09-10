package nextstep.jwp.response;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private static final String DELIMITER = "\r\n";

    private final Map<String, String> headers;

    public ResponseHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public void save(final String name, final String value) {
        headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String toResponse() {
        final List<String> headerStrings = headers.keySet()
                .stream()
                .map(key -> key + ": " + headers.get(key) + " ")
                .collect(Collectors.toList());

        return String.join(DELIMITER, headerStrings);
    }
}
