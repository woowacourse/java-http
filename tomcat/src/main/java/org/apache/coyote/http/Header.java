package org.apache.coyote.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.util.Constants.KEY_INDEX;
import static org.apache.coyote.util.Constants.VALUE_INDEX;

public abstract class Header {

    protected static final String HEADER_SEPARATOR = ":";
    protected static final String HEADER_DELIMITER = ": ";
    protected static final String CONTENT_LENGTH = "Content-Length";


    protected final Map<String, String> headers;

    protected Header(List<String> splitHeaders) {
        this(splitHeaders.stream()
                .filter(header -> header.contains(HEADER_SEPARATOR))
                .map(header -> header.split(HEADER_SEPARATOR))
                .collect(Collectors.toMap(header -> header[KEY_INDEX].trim(), header -> header[VALUE_INDEX].trim())));
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
}
