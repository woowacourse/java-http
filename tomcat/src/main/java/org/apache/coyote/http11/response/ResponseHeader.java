package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeader {

    private final Map<String, String> headers;

    public ResponseHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeader of(String contentType, String contentLength) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", contentType);
        header.put("Content-Length", contentLength);

        return new ResponseHeader(header);
    }

    public void addHeader(String headerType, String headerValue) {
        headers.put(headerType, headerValue);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
