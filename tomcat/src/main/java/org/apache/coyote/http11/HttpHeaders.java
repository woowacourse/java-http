package org.apache.coyote.http11;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String HEADER_DELIMITER = ":";
    private static final int SPLIT_LIMIT = 2;

    private final Map<String, Object> fields = new HashMap<>();

    public HttpHeaders(Collection<String> headerLines) {
        headerLines.stream()
                .map(headerLine -> headerLine.split(HEADER_DELIMITER, SPLIT_LIMIT))
                .filter(headerToken -> headerToken.length == SPLIT_LIMIT)
                .forEach(headerToken -> fields.put(headerToken[0].trim(), headerToken[1].trim()));
    }

    public HttpHeaders() {
        this(Collections.emptyList());
    }

    public void put(String name, Object value) {
        fields.put(name, value);
    }

    public Object get(String name) {
        return fields.get(name);
    }

    public void setContentType(ContentType contentType) {
        fields.put(CONTENT_TYPE, contentType.getMediaType());
    }

    public void setLocation(String location) {
        fields.put(LOCATION, location);
    }

    public void setContentLength(long contentLength) {
        fields.put(CONTENT_LENGTH, contentLength);
    }

    public long getContentLength() {
        return (long) fields.getOrDefault(CONTENT_LENGTH, 0L);
    }

    public Map<String, Object> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    @Override
    public String toString() {
        return fields.toString();
    }
}
