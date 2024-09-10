package org.apache.coyote.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Header {

    protected static final String HEADER_SEPARATOR = ":";
    protected static final String HEADER_DELIMITER = ": ";
    protected static final String CONTENT_LENGTH = "Content-Length";

    protected final Map<String, String> headers;

    protected Header(List<String> splitHeaders) {
        this(splitHeaders.stream()
                .filter(header -> header.contains(HEADER_SEPARATOR))
                .collect(Collectors.toMap(
                        header -> header.substring(0, header.indexOf(HEADER_SEPARATOR)).trim(),
                        header -> header.substring(header.indexOf(HEADER_SEPARATOR) + 1).trim())));
    }

    protected Header(Map<String, String> headers) {
        this.headers = headers;
    }

    protected Header() {
        this(new HashMap<>());
    }

    public boolean hasHeader(String header) {
        return headers.containsKey(header);
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }
}
