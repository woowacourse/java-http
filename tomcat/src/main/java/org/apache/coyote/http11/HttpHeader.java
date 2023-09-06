package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpHeader {

    public static final String HEADER_DELIMITER = ": ";

    private final Map<String, String> headers;

    private HttpHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeader emptyHeader() {
        return new HttpHeader(new LinkedHashMap<>());
    }

    public static HttpHeader of(final List<String> requestHeader) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (int i = 1; i < requestHeader.size(); i++) {
            final String[] splitHeader = requestHeader.get(i).split(HEADER_DELIMITER);
            headers.put(splitHeader[0], splitHeader[1]);
        }
        return new HttpHeader(headers);
    }

    public String get(final String header) {
        return headers.get(header);
    }

    public boolean hasHeader(final String header) {
        return headers.containsKey(header);
    }

    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }

    public boolean isEmpty() {
        return headers.isEmpty();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
