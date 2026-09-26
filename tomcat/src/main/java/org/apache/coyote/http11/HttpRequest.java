package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String protocolVersion;
    private final Map<String, String> queryParameters = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> body = new HashMap<>();

    public HttpRequest(BufferedReader reader) throws IOException {
        String[] parts = requestLine(reader).split(" ", 3);
        this.method = parts[0];
        this.path = path(parts[1]);
        this.protocolVersion = path(parts[2]);
        findHeaders(reader);
        findBody(reader);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public boolean hasHeader(String key) {
        return headers.containsKey(key.toUpperCase());
    }

    public String getHeaderValue(String key) {
        return headers.get(key.toUpperCase());
    }

    public boolean hasBody() {
        return !body.isEmpty();
    }

    public String getBodyValue(String key) {
        return body.get(key);
    }

    public String getQueryParameterValue(String key) {
        return queryParameters.get(key);
    }

    private String requestLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private void findBody(BufferedReader reader) throws IOException {
        if (!headers.containsKey("Content-Length".toUpperCase()) || headers.get("Content-Length".toUpperCase())
                .equals("0")) {
            return;
        }
        int length = Integer.parseInt(headers.get("Content-Length".toUpperCase()));
        char[] body = new char[length];
        reader.read(body, 0, length);

        final String[] parts = new String(body).split("&");
        for (String part : parts) {
            String[] pair = part.split("=", 2);
            this.body.put(pair[0], pair[1]);
        }
    }

    private String path(String path) {
        if (!path.contains("?")) {
            return path;
        }
        String queryString = List.of(path.split("\\?")).getLast();
        final String[] queries = queryString.split("&");
        for (String query : queries) {
            String[] pair = query.split("=", 2);
            queryParameters.put(pair[0], pair[1]);
        }
        return List.of(path.split("\\?")).getFirst();
    }

    private void findHeaders(BufferedReader reader) throws IOException {
        String line;
        while (!(line = reader.readLine()).isBlank()) {
            if (line.matches("[^:]+:\\s*.*")) {
                String[] parts = line.split(":", 2);

                String key = parts[0].trim().toUpperCase();
                String value = parts[1].trim();

                headers.put(key, value);
            }
        }
    }
}
