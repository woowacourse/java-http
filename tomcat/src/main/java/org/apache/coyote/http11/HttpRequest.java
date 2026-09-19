package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> queryParameters;
    private final Map<String, String> headers;

    private HttpRequest(String method, String path, String version,
                        Map<String, String> queryParameters, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.queryParameters = queryParameters;
        this.headers = headers;
    }

    public static Optional<HttpRequest> from(BufferedReader reader) throws IOException {
        final String requestLine = reader.readLine();
        if (requestLine == null) {
            return Optional.empty();
        }
        final String[] tokens = requestLine.split(" ");
        if (tokens.length != 3) {
            throw new HttpRequestParseException("요청 라인 형식이 잘못되었습니다: " + requestLine);
        }
        final String uri = tokens[1];
        return Optional.of(new HttpRequest(
                tokens[0], extractPath(uri), tokens[2], parseQueryParameters(uri), parseHeaders(reader)));
    }

    private static String extractPath(String uri) {
        final int idx = uri.indexOf("?");
        if (idx == -1) {
            return uri;
        }
        return uri.substring(0, idx);
    }

    private static Map<String, String> parseQueryParameters(String uri) {
        final Map<String, String> parameters = new HashMap<>();
        final int idx = uri.indexOf("?");
        if (idx == -1) {
            return parameters;
        }
        for (String pair : uri.substring(idx + 1).split("&")) {
            putQueryParameter(parameters, pair);
        }
        return parameters;
    }

    private static void putQueryParameter(Map<String, String> parameters, String pair) {
        final int idx = pair.indexOf("=");
        if (idx == -1) {
            parameters.put(pair, "");
            return;
        }
        parameters.put(pair.substring(0, idx), pair.substring(idx + 1));
    }

    private static Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            putHeader(headers, line);
            line = reader.readLine();
        }
        return headers;
    }

    private static void putHeader(Map<String, String> headers, String line) {
        final int idx = line.indexOf(":");
        if (idx == -1) {
            throw new HttpRequestParseException("헤더 형식이 잘못되었습니다: " + line);
        }
        headers.put(line.substring(0, idx).trim(), line.substring(idx + 1).trim());
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public String getQueryParameter(String name) {
        return queryParameters.get(name);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }
}
