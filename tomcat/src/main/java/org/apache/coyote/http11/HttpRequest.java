package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private final Map<String, String> bodyParams;

    private HttpRequest(final String method, final String path, final Map<String, String> queryParams,
                        final Map<String, String> headers, final Map<String, String> bodyParams) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.headers = headers;
        this.bodyParams = bodyParams;
    }

    public static HttpRequest from(final String requestLine) {
        final String[] tokens = requestLine.split(" ");
        final String method = tokens[0];
        final String uri = tokens[1];
        final String path = extractPath(uri);
        final Map<String, String> queryParams = extractQueryParams(uri);
        return new HttpRequest(method, path, queryParams,
                new TreeMap<>(String.CASE_INSENSITIVE_ORDER), new HashMap<>());
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("HTTP 요청 줄을 읽을 수 없습니다.");
        }
        final HttpRequest request = from(requestLine);

        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            final String[] header = headerLine.split(":", 2);
            request.headers.put(header[0].trim(), header[1].trim());
        }
        request.bodyParams.putAll(readBodyParams(reader, request.headers));
        return request;
    }

    private static Map<String, String> readBodyParams(final BufferedReader reader,
                                                       final Map<String, String> headers) throws IOException {
        final String contentLengthHeader = headers.get("Content-Length");
        if (contentLengthHeader == null) {
            return Map.of();
        }

        final int contentLength = Integer.parseInt(contentLengthHeader);
        final char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            final int read = reader.read(buffer, totalRead, contentLength - totalRead);
            if (read == -1) {
                throw new IOException("요청 본문이 Content-Length보다 짧습니다.");
            }
            totalRead += read;
        }
        return extractFormParams(new String(buffer));
    }

    private static Map<String, String> extractFormParams(final String requestBody) {
        final Map<String, String> params = new HashMap<>();
        for (String param : requestBody.split("&")) {
            final String[] keyAndValue = param.split("=", 2);
            final String key = URLDecoder.decode(keyAndValue[0], StandardCharsets.UTF_8);
            final String value = keyAndValue.length == 2
                    ? URLDecoder.decode(keyAndValue[1], StandardCharsets.UTF_8)
                    : "";
            params.put(key, value);
        }
        return params;
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

    public Map<String, String> getBodyParams() {
        return bodyParams;
    }

    public HttpCookie getCookies() {
        return HttpCookie.from(getHeader("Cookie"));
    }
}
