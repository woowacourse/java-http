package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpCookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String SEPARATOR = ":";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private final Map<String, String> values;

    private HttpHeaders(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpHeaders from(final BufferedReader reader) throws IOException {
        final Map<String, String> values = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            final String[] keyAndValue = line.split(SEPARATOR, 2);
            if (keyAndValue.length == 2) {
                values.put(keyAndValue[0].trim(), keyAndValue[1].trim());
            }
        }
        return new HttpHeaders(values);
    }

    public String get(final String key) {
        return values.get(key);
    }

    public int getContentLength() {
        final String contentLength = values.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(values.get(COOKIE));
    }
}