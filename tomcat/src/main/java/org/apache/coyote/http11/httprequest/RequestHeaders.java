package org.apache.coyote.http11.httprequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private static final String HEADER_DELIMITER = ": ";

    private final Map<String, String> headers;

    public RequestHeaders(List<String> headers) {
        this.headers = toMap(headers);
    }

    private Map<String, String> toMap(List<String> headerLines) {
        Map<String, String> headers = new LinkedHashMap<>();

        for (String headerLine : headerLines) {
            String[] headerInfo = headerLine.split(HEADER_DELIMITER);
            headers.put(headerInfo[0], headerInfo[1]);
        }

        return headers;
    }

    public int getContentLength() {
        String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }
}
