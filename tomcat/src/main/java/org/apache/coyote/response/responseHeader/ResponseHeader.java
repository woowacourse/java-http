package org.apache.coyote.response.responseHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeader {

    public static final String HEADER_DELIMITER = "\r\n";
    private static final String HEADER_COMBINATOR = ": ";
    private static final String ENCODING_TYPE = ";charset=utf-8";

    private Map<String, String> headers;

    public ResponseHeader() {
        this.headers = new HashMap<>();
    }

    public ResponseHeader init(final int length, final ContentType contentType) {
        this.headers = initHeaders(length, contentType);
        return this;
    }

    private Map<String, String> initHeaders(final int bodyLength, final ContentType contentType) {
        headers.put(
                HttpHeaders.CONTENT_TYPE.getValue(),
                contentType.isImage()
                        ? contentType.getContentType()
                        : contentType.getContentType() + ENCODING_TYPE
        );

        headers.put(HttpHeaders.CONTENT_LENGTH.getValue(), String.valueOf(bodyLength));

        return headers;
    }

    public void addCookie(String cookies) {
        headers.put(HttpHeaders.SET_COOKIE.getValue(), cookies);
    }

    public String toCombine() {
        return headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + HEADER_COMBINATOR + entry.getValue() + " ")
                .collect(Collectors.joining(HEADER_DELIMITER));
    }

    public void addLocation(final String location) {
        headers.put(HttpHeaders.LOCATION.getValue(), location);
    }
}
