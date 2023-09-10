package nextstep.jwp.response;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private static final String DELIMITER = "\r\n";

    private final LinkedHashMap<String, String> headers;

    private ResponseHeaders(final LinkedHashMap<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders from(final LinkedHashMap<String, String> headers) {
        return new ResponseHeaders(headers);
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
