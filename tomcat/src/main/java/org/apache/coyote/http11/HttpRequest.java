package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;

    private HttpRequest(final String method, final String path, final Map<String, String> queryParams,
                        final Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.headers = headers;
    }

    public static HttpRequest from(final String requestLine) {
        final String[] tokens = requestLine.split(" ");
        final String method = tokens[0];
        final String uri = tokens[1];
        final String path = extractPath(uri);
        final Map<String, String> queryParams = extractQueryParams(uri);
        return new HttpRequest(method, path, queryParams, new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final HttpRequest request = from(reader.readLine());

        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            final String[] header = headerLine.split(":", 2);
            request.headers.put(header[0].trim(), header[1].trim());
        }
        return request;
    }

    private static String extractPath(final String uri) {
        if (uri.contains("?")) {
            return uri.substring(0, uri.indexOf("?"));
        }
        return uri;
    }

    private static Map<String, String> extractQueryParams(final String uri) {
        final Map<String, String> params = new HashMap<>();
        if (!uri.contains("?")) {
            return params;
        }
        final String queryString = uri.substring(uri.indexOf("?") + 1);
        for (String param : queryString.split("&")) {
            String[] kv = param.split("=");
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

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }
}
