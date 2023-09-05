package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers;

    public ResponseHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType);
    }

    public void setContentLength(int length) {
        headers.put("Content-Length", String.valueOf(length));
    }

    public void setLocation(String location) {
        headers.put("Location", location);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> header : headers.entrySet()){
            sb.append(header.getKey() + ": " + header.getValue() + " " + "\r\n");
        }
        return sb.toString();
    }
}
