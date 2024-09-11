package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

    private static final String RESPONSE_HEADER_FORMAT = "%s: %s \r\n";
    private final Map<String, String> headers;

    public HttpHeader() {
        this.headers = new HashMap<>();
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHttpHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        headers.forEach((key, value) -> stringBuilder.append(String.format(RESPONSE_HEADER_FORMAT, key, value)));
        return stringBuilder.toString();
    }
}
