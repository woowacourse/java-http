package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String DELIMITER = ":";
    private static final int NAME_VALUE_SIZE = 2;
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private final Map<String, String> values;

    private HttpHeaders(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpHeaders from(final BufferedReader reader) throws IOException {
        final Map<String, String> values = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            final String[] nameAndValue = line.split(DELIMITER, NAME_VALUE_SIZE);
            if (nameAndValue.length == NAME_VALUE_SIZE) {
                values.put(nameAndValue[0].trim(), nameAndValue[1].trim());
            }
        }
        return new HttpHeaders(values);
    }

    public String get(final String name) {
        return values.get(name);
    }

    public int getContentLength() {
        final String contentLength = values.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public String getCookieHeader() {
        return values.get(COOKIE);
    }
}
