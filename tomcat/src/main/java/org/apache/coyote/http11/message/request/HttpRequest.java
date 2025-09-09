package org.apache.coyote.http11.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.message.HttpCookie;
import org.apache.coyote.http11.message.HttpHeaders;

public class HttpRequest {
    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final HttpHeaders headers;
    private final String version;
    private final String body;

    private HttpRequest(String method, String path, Map<String, String> queryParams, HttpHeaders headers, String version,
                       String body) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.headers = headers;
        this.version = version;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        return parse(reader);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getVersion() {
        return version;
    }

    public boolean equalMethod(String method) {
        return this.method.equals(method);
    }

    public boolean equalContentType(final String mimeType) {
        final String contentType = headers.getFirst("Content-Type");
        return contentType.equals(mimeType);
    }

    public Map<String, String> getFormParams() {
        if (equalContentType("application/x-www-form-urlencoded")) {
            return parseUrlEncoded(body);
        }
        return Collections.emptyMap();
    }

    public boolean hasSessionCookie() {
        HttpCookie cookie = HttpCookie.from(headers);
        return cookie.contains("JSESSIONID");
    }

    private static HttpRequest parse(final BufferedReader reader) throws IOException {
        final String requestLine = reader.readLine();

        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("Empty request line");
        }

        final String[] parts = requestLine.split(" ");

        validateRequestLine(parts, requestLine);

        final String method = parts[0];
        final String requestUri = parts[1];
        final String version = parts[2];

        final HttpHeaders headers = new HttpHeaders();
        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            String[] headerParts = line.split(": ", 2);
            headers.addHeader(headerParts[0], headerParts[1]);
        }

        String path = requestUri;
        if (requestUri.contains("?")) {
            final int index = requestUri.indexOf("?");
            path = requestUri.substring(0, index);
            final String queryString = requestUri.substring(index + 1);
            return new HttpRequest(method, path, parseUrlEncoded(queryString), headers, version, null);
        }

        String body = null;
        if ("POST".equals(method)) {
            int contentLength = Integer.parseInt(headers.getContentLength());
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                reader.read(bodyChars, 0, contentLength);
                body = new String(bodyChars);
            }
            return new HttpRequest(method, path, Collections.emptyMap(), headers, version, body);
        }

        return new HttpRequest(method, path, Collections.emptyMap(), headers, version, null);
    }

    private static void validateRequestLine(String[] parts, String requestLine) {
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid HTTP request line: " + requestLine);
        }
    }

    private static Map<String, String> parseUrlEncoded(final String input) {
        final Map<String, String> params = new HashMap<>();
        final String[] pairs = input.split("&");

        for (String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }
}
