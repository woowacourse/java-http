package org.apache.catalina.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeader {

    private static final String HEADER_DELIMITER = ": ";

    private final Map<Header, String> headers;

    private RequestHeader(Map<Header, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader parse(List<String> rawHeaders) {
        Map<Header, String> headers = new HashMap<>();
        for (String rawHeader : rawHeaders) {
            String[] keyValue = rawHeader.split(HEADER_DELIMITER);
            headers.put(Header.of(keyValue[0]), keyValue[1]);
        }

        return new RequestHeader(headers);
    }

    public boolean notContainsContentLength() {
        return !headers.containsKey(Header.CONTENT_LENGTH);
    }

    public int getContentLength() {
        String contentLength = headers.get(Header.CONTENT_LENGTH);
        return Integer.parseInt(contentLength);
    }

    public String getCookie() {
        return headers.get(Header.COOKIE);
    }
}
