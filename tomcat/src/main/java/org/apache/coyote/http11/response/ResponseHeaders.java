package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.request.Request.SPACE_DELIMITER;
import static org.apache.coyote.http11.request.RequestHeaders.HEADER_KEY_VALUE_DELIMITER;

import java.util.Map;
import java.util.Map.Entry;

public class ResponseHeaders {

    private final Map<String, String> headers;

    public ResponseHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public void add(final String key, final String value) {
        headers.put(key, value);
    }

    public String headerToString() {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey()).append(HEADER_KEY_VALUE_DELIMITER + SPACE_DELIMITER).append(entry.getValue()).append(" \r\n");
        }
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }
}
