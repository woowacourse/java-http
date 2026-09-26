package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpCookie;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaders {

    private static final String JSESSION_ID_KEY = "JSESSIONID";

    private final Map<String, String> headers = new LinkedHashMap<>();

    public ResponseHeaders() {
    }

    public ResponseHeaders(final Path filePath, final String location, final HttpCookie httpCookie) {
        add("Content-Type", getContentType(filePath));

        if (location != null) {
            add("Location", location);
        }
        if (httpCookie != null && httpCookie.contains(JSESSION_ID_KEY)) {
            add("Set-Cookie", JSESSION_ID_KEY + "=" + httpCookie.get(JSESSION_ID_KEY));
        }
    }

    public void add(final String name, final String value) {
        headers.put(name, value);
    }

    public String get(final String name) {
        return headers.get(name);
    }

    public boolean contains(final String name) {
        return headers.containsKey(name);
    }

    public String toHttpMessage(final int contentLength) {
        final StringBuilder httpHeaders = new StringBuilder();

        if (contains("Content-Type")) {
            appendHeader(httpHeaders, "Content-Type", get("Content-Type"));
        }
        appendHeader(httpHeaders, "Content-Length", String.valueOf(contentLength));

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey().equals("Content-Type") || header.getKey().equals("Content-Length")) {
                continue;
            }
            appendHeader(httpHeaders, header.getKey(), header.getValue());
        }

        return httpHeaders.toString();
    }

    private void appendHeader(final StringBuilder httpHeaders, final String name, final String value) {
        httpHeaders.append(name).append(": ").append(value).append(" ").append("\r\n");
    }

    private String getContentType(final Path filePath) {
        final String path = filePath.toString();

        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        if (path.endsWith(".js")) {
            return "text/javascript;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }
}
