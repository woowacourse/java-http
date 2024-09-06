package org.apache.coyote.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.http.Constants.*;

public class Header {

    private final Map<String, String> headers;

    public static Header of(String bulkHeaders) {
        List<String> splitHeaders = List.of(bulkHeaders.split(CRLF));
        return of(splitHeaders);
    }

    public static Header of(List<String> splitHeaders) {
        Map<String, String> headers = splitHeaders.stream()
                .filter(header -> header.contains(COLON))
                .map(header -> header.split(COLON_WITH_SPACE))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
        return new Header(headers);
    }

    public Header() {
        this(new HashMap<>());
    }

    private Header(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean hasHeader(String header) {
        return headers.containsKey(header);
    }

    public String getValue(String header) {
        if (!headers.containsKey(header)) {
            throw new IllegalArgumentException("Header " + header + " not found");
        }
        return headers.get(header);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String toResponse() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey().concat(COLON_WITH_SPACE).concat(entry.getValue()).concat(SPACE))
                .collect(Collectors.joining(CRLF));
    }
}
