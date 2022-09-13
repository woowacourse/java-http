package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {

    public static final String HEADER_KEY_VALUE_DELIMITER = ":";
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<String, String> headers;

    private RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders of(final BufferedReader bufferedReader) throws IOException {
        String headerKeyValue = bufferedReader.readLine();
        final HashMap<String, String> headers = new HashMap<>();
        while (headerKeyValue != null && !headerKeyValue.isBlank()) {
            final String[] splitHeaderKeyValue = headerKeyValue.split(HEADER_KEY_VALUE_DELIMITER);
            headers.put(splitHeaderKeyValue[KEY], splitHeaderKeyValue[VALUE]);
            headerKeyValue = bufferedReader.readLine();
        }
        return new RequestHeaders(headers);
    }

    public int getContentLength() {
        final String contentLength = headers.get("Content-Length");
        if (contentLength != null) {
            return Integer.parseInt(contentLength.trim());
        }
        return 0;
    }

    public boolean hasJsessionid() {
        final String cookie = headers.get("Cookie");
        if (cookie == null) {
            return false;
        }
        return Arrays.stream(cookie.split("; "))
                .anyMatch(tuple -> tuple.contains("JSESSIONID="));
    }
}
