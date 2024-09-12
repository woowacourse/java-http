package org.apache.coyote.response.header;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.coyote.util.ContentType;
import org.apache.coyote.util.HttpHeaders;

public class ResponseHeader {

    private static final String HEADER_DELIMITER = "\r\n";
    private static final String HEADER_COMBINATOR = ": ";
    private static final String SPACE = " ";

    private final Map<String, String> headers;

    public ResponseHeader() {
        headers = new HashMap<>();
    }

    public String toCombinedHeader() {
        return headers.entrySet()
                .stream()
                .map(this::makeSingleHeader)
                .collect(Collectors.joining(HEADER_DELIMITER));
    }

    private String makeSingleHeader(Entry<String, String> entry) {
        return entry.getKey() + HEADER_COMBINATOR + entry.getValue() + SPACE;
    }

    public void addLocation(String location) {
        headers.put(HttpHeaders.LOCATION.getValue(), location);
    }

    public void addContentLengthAndType(ContentType contentType, int bodyLength) {
        addContentType(contentType);
        addContentLength(bodyLength);
    }

    private void addContentType(ContentType contentType) {
        headers.put(HttpHeaders.CONTENT_TYPE.getValue(), contentType.getContentType());
    }

    private void addContentLength(int bodyLength) {
        headers.put(HttpHeaders.CONTENT_LENGTH.getValue(), String.valueOf(bodyLength));
    }

    public void addCookie(String cookies) {
        headers.put(HttpHeaders.SET_COOKIE.getValue(), cookies);
    }
}
