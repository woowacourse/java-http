package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class HttpHeaders {

    private static final String DELIMITER = ":";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private final Map<String, String> values;

    private HttpHeaders(Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    static HttpHeaders from(BufferedReader reader) throws IOException {
        Map<String, String> values = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] keyValue = line.split(DELIMITER, 2);
            if (keyValue.length == 2) {
                values.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return new HttpHeaders(values);
    }

    String get(String name) {
        return values.get(name);
    }

    int getContentLength() {
        String contentLength = values.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    HttpCookie getCookie() {
        return new HttpCookie(values.get(COOKIE));
    }
}
