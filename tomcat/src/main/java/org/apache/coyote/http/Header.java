package org.apache.coyote.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.util.Constants.CRLF;
import static org.apache.coyote.util.Constants.SPACE;

public abstract class Header {

    public static final String HEADER_SEPARATOR = ":";
    public static final String HEADER_DELIMITER = ": ";
    protected static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;

    protected Header(List<String> splitHeaders) {
        this(splitHeaders.stream()
                .filter(header -> header.contains(HEADER_SEPARATOR))
                .map(header -> header.split(HEADER_SEPARATOR))
                .collect(Collectors.toMap(arr -> arr[0].trim(), arr -> arr[1].trim())));
    }

    protected Header() {
        this(new HashMap<>());
    }

    protected Header(Map<String, String> headers) {
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

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String toResponse() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + HEADER_DELIMITER + entry.getValue() + SPACE)
                .collect(Collectors.joining(CRLF));
    }
}
