package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String queryString;
    private final Map<String, String> params;
    private final Map<String, String> headers;

    public HttpRequest(BufferedReader reader) throws IOException {
        String requestLine = readRequestLine(reader);
        String[] tokens = requestLine.split(" ");
        this.method = tokens[0];
        String uri = tokens[1];

        this.path = extractPath(uri);
        this.queryString = extractQueryString(uri);
        this.headers = Collections.unmodifiableMap(readHeaders(reader));
        
        if ("POST".equalsIgnoreCase(this.method) && headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] body = new char[contentLength];
            reader.read(body, 0, contentLength);
            this.params = Collections.unmodifiableMap(parseParams(new String(body)));
            return;
        }
        
        this.params = Collections.unmodifiableMap(parseParams(this.queryString));
    }

    private String readRequestLine(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            throw new IOException("Empty Request");
        }
        return requestLine;
    }

    private String extractPath(String uri) {
        if (!uri.contains("?")) {
            return uri;
        }
        return uri.substring(0, uri.indexOf("?"));
    }

    private String extractQueryString(String uri) {
        if (!uri.contains("?")) {
            return "";
        }
        return uri.substring(uri.indexOf("?") + 1);
    }

    private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int sep = line.indexOf(":");
            if (sep <= 0) {
                continue;
            }
            String key = line.substring(0, sep).trim();
            String value = line.substring(sep + 1).trim();
            headers.put(key, value);
        }
        return headers;
    }

    private Map<String, String> parseParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length < 2) {
                continue;
            }
            params.put(kv[0], kv[1]);
        }
        return params;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public boolean isParams() {
        return !params.isEmpty();
    }

    public Map<String, String> getParams() {
        return params;
    }
}
