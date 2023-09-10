package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Header {

    private static final String HEADER_LINE_DELIMITER = System.lineSeparator();
    private static final String HEADER_DELIMITER = ": ";

    private final Map<String, String> headers;

    public Header() {
        this.headers = new LinkedHashMap<>();
    }

    public Header(Map<String, String> headers) {
        this.headers = headers;
    }

    public static Header create(String header) {
        Map<String, String> headers = new LinkedHashMap<>();

        String[] splitHeader = header.split(HEADER_LINE_DELIMITER);
        for (String eachHeader : splitHeader) {
            String[] splitEachHeader = eachHeader.split(HEADER_DELIMITER);
            String headerName = splitEachHeader[0];
            String headerValue = splitEachHeader[1];
            headers.put(headerName, headerValue);
        }
        return new Header(headers);
    }

    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public Optional<String> findHeaderValueByKey(String key) {
        if (headers.containsKey(key)) {
            return Optional.of(headers.get(key));
        }
        return Optional.empty();
    }
}
