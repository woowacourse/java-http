package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaders {

    private static final String CRLF = "\r\n";
    private static final String SPACE = " ";
    private static final String DELIMITER = ": ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";


    private final Map<String, String> headers;

    public ResponseHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public void setContentType(final String contentType) {
        headers.put(CONTENT_TYPE, contentType);
    }

    public void setContentLength(final int length) {
        headers.put(CONTENT_LENGTH, String.valueOf(length));
    }

    public void setLocation(final String location) {
        headers.put(LOCATION, location);
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> header : headers.entrySet()){
            sb.append(header.getKey() + DELIMITER + header.getValue() + SPACE + CRLF);
        }
        return sb.toString();
    }
}
