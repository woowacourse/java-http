package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpCookie;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = Collections.unmodifiableMap(headers);
    }

    public static HttpHeaders of(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":", 2);
            String key = parts[0].trim().toLowerCase();
            String value = parts[1].trim();
            headers.put(key, value);
        }
        return new HttpHeaders(headers);
    }

    public HttpCookie getHttpCookie() {
        String cookieLine = headers.getOrDefault("cookie", "");
        return HttpCookie.from(cookieLine);
    }

    public boolean hasJSessionId() {
        return !getJSessionId().isEmpty();
    }

    public String getJSessionId() {
        return getHttpCookie().getJSessionId();
    }

    public int getContentLength() {
        String contentLength = headers.getOrDefault("content-length", "0");
        return Integer.parseInt(contentLength);
    }

    public ContentType getContentType() {
        String acceptLine = headers.getOrDefault("accept", "");
        return Arrays.stream(ContentType.values())
                .filter(type -> acceptLine.contains(type.getName()))
                .findFirst()
                .orElse(ContentType.HTML);
    }
}
