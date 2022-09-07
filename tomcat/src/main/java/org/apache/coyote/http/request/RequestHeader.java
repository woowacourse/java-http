package org.apache.coyote.http.request;

import static org.apache.coyote.http.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.http.HttpHeaders.COOKIE;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {

    private static final String HEADER_DELIMITER = ": ";
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<String, Object> values;

    private RequestHeader(final Map<String, Object> values) {
        this.values = values;
    }

    public static RequestHeader from(final BufferedReader bufferedReader) throws IOException {
        final Map<String, Object> values = new HashMap<>();
        values.put(CONTENT_LENGTH.getValue(), 0);

        String header;
        while (!"".equals((header = bufferedReader.readLine()))) {
            final String[] splitHeader = header.split(HEADER_DELIMITER);
            if (splitHeader[KEY].equals(COOKIE.getValue())) {
                final RequestCookie cookie = RequestCookie.from(splitHeader[VALUE].trim());
                values.put(splitHeader[KEY], cookie);
                continue;
            }
            values.put(splitHeader[KEY], splitHeader[VALUE].trim());
        }

        return new RequestHeader(values);
    }

    public boolean hasSessionId() {
        if (!values.containsKey(COOKIE.getValue())) {
            return false;
        }

        final RequestCookie cookie = (RequestCookie) values.get(COOKIE.getValue());
        return cookie.hasSessionId();
    }

    public String getSessionId() {
        final RequestCookie cookie = (RequestCookie) values.get(COOKIE.getValue());
        return cookie.getSessionId();
    }

    public int getContentLength() {
        final Object contentLength = values.get(CONTENT_LENGTH.getValue());
        if (contentLength instanceof Integer) {
            return (int) contentLength;
        }
        return Integer.parseInt((String) contentLength);
    }
}
