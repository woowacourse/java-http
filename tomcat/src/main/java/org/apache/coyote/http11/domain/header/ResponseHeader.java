package org.apache.coyote.http11.domain.header;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.domain.body.ContentType;

public class ResponseHeader {

    public static final String HEADER_DELIMITER = "\r\n";
    private static final String HEADER_COMBINATOR = ": ";
    private static final String ENCODING_TYPE = ";charset=utf-8";

    private final Map<String, String> headers;

    public ResponseHeader(int bodyLength, ContentType contentType) {
        this.headers = initHeaders(bodyLength, contentType);
    }

    private Map<String, String> initHeaders(int bodyLength, ContentType contentType) {
        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Length", String.valueOf(bodyLength));
        headers.put("Content-Type", contentType.getContentType() + ENCODING_TYPE);

        return headers;
    }

    public String toCombinedHeader() {
        return headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + HEADER_COMBINATOR + entry.getValue() + " ")
                .collect(Collectors.joining(HEADER_DELIMITER));
    }
}
