package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    public HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        if (bufferedReader == null) {
            throw new IllegalArgumentException("BufferedReader cannot be null.");
        }

        final String requestLine = readRequestLine(bufferedReader);
        final String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 2) {
            throw new IOException("Invalid: malformed request line. " + requestLine);
        }

        final String uri = requestParts[1];
        final String path = parsePath(uri);
        final Map<String, String> queryParams = parseQueryParams(uri);

        return new HttpRequest(path, queryParams);
    }

    private String readRequestLine(BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Invalid: request line is null or empty.");
        }
        return requestLine;
    }

    private String parsePath(String uri) {
        if (uri.contains("?")) {
            return uri.substring(0, uri.indexOf("?"));
        }
        return uri;
    }

    private Map<String, String> parseQueryParams(String uri) {
        if (!uri.contains("?")) {
            return Collections.emptyMap();
        }

        final String queryString = uri.substring(uri.indexOf("?") + 1);
        final Map<String, String> params = new HashMap<>();
        for (String pair : queryString.split("&")) {
            final String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }
}
