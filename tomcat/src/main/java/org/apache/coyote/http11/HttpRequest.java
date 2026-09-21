package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class HttpRequest {

    private static final String CONTENT_LENGTH = "content-length";
    private static final String CONTENT_TYPE = "content-type";
    private static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    private static final Set<String> VERSIONS = Set.of("HTTP/1.0", "HTTP/1.1");

    private final HttpMethod method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final Map<String, List<String>> queryParams;
    private final Map<String, List<String>> bodyParams;
    private final String body;

    private HttpRequest(
            HttpMethod method,
            String path,
            String version,
            Map<String, String> headers,
            Map<String, List<String>> queryParams,
            Map<String, List<String>> bodyParams,
            String body
    ) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = Map.copyOf(headers);
        this.queryParams = copyParams(queryParams);
        this.bodyParams = copyParams(bodyParams);
        this.body = body;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        String[] tokens = parseRequestLine(inputStream);

        HttpMethod method = HttpMethod.valueOf(tokens[0]);
        String target = tokens[1];
        String version = tokens[2];

        if (!VERSIONS.contains(version)) {
            throw new IllegalArgumentException("지원하지 않는 HTTP Version입니다: " + version);
        }

        String path = parsePath(target);

        Map<String, List<String>> queryParams = parseQueryParams(target);
        Map<String, String> headers = parseHeaders(inputStream);
        int contentLength = parseContentLength(headers);
        String body = readBody(inputStream, contentLength);
        Map<String, List<String>> bodyParams = parseBodyParams(headers, body);

        return new HttpRequest(
                method,
                path,
                version,
                headers,
                queryParams,
                bodyParams,
                body
        );
    }

    private static String[] parseRequestLine(InputStream inputStream) throws IOException {
        String requestLine = readLine(inputStream);
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("요청 라인이 존재하지 않습니다.");
        }

        String[] tokens = requestLine.trim().split("\\s+");
        if (tokens.length != 3) {
            throw new IllegalArgumentException("잘못된 HTTP 요청 라인입니다: " + requestLine);
        }

        return tokens;
    }

    private static String parsePath(String target) {
        int queryIndex = target.indexOf('?');
        if (queryIndex == -1) {
            return target;
        }

        return target.substring(0, queryIndex);
    }

    private static Map<String, List<String>> parseQueryParams(String target) {
        int queryIndex = target.indexOf('?');
        if (queryIndex == -1 || queryIndex == target.length() - 1) {
            return Map.of();
        }

        String queryString = target.substring(queryIndex + 1);

        return parseParams(queryString);
    }

    private static Map<String, String> parseHeaders(InputStream inputStream) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;
        while ((line = readLine(inputStream)) != null && !line.isEmpty()) {
            String[] tokens = line.split(":", 2);

            if (tokens.length != 2) {
                continue;
            }

            String name = tokens[0].trim().toLowerCase(Locale.ROOT);
            String value = tokens[1].trim();
            headers.put(name, value);
        }

        return headers;
    }

    private static int parseContentLength(Map<String, String> headers) {
        String value = headers.getOrDefault(CONTENT_LENGTH, "0");

        try {
            int contentLength = Integer.parseInt(value);
            if (contentLength < 0) {
                throw new IllegalArgumentException("잘못된 Content-Length입니다: " + value);
            }

            return contentLength;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 Content-Length입니다: " + value);
        }
    }

    private static String readBody(InputStream inputStream, int contentLength) throws IOException {
        if (contentLength == 0) {
            return "";
        }

        byte[] body = inputStream.readNBytes(contentLength);

        if (body.length != contentLength) {
            throw new IOException(
                    "요청 Body가 Content-Length보다 짧습니다. "
                            + "expected=" + contentLength
                            + ", actual=" + body.length
            );
        }

        return new String(body, StandardCharsets.UTF_8);
    }

    private static Map<String, List<String>> parseBodyParams(Map<String, String> headers, String body) {
        if (body.isBlank() || !isFormUrlEncoded(headers)) {
            return Map.of();
        }

        return parseParams(body);
    }

    private static Map<String, List<String>> parseParams(String rawParams) {
        Map<String, List<String>> params = new HashMap<>();

        if (rawParams == null || rawParams.isBlank()) {
            return params;
        }

        for (String param : rawParams.split("&")) {
            String[] pair = param.split("=", 2);

            String key = decode(pair[0]);
            String value = pair.length == 2 ? decode(pair[1]) : "";

            params.computeIfAbsent(key, ignored -> new ArrayList<>())
                    .add(value);
        }

        return params;
    }

    private static boolean isFormUrlEncoded(Map<String, String> headers) {
        String contentType = headers.get(CONTENT_TYPE);

        if (contentType == null) {
            return false;
        }

        return contentType.toLowerCase(Locale.ROOT)
                .startsWith(FORM_URL_ENCODED);
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int current;
        while ((current = inputStream.read()) != -1) {
            if (current == '\n') {
                break;
            }

            buffer.write(current);
        }

        if (current == -1 && buffer.size() == 0) {
            return null;
        }

        byte[] bytes = buffer.toByteArray();
        int length = bytes.length;

        if (length > 0 && bytes[length - 1] == '\r') {
            length--;
        }

        return new String(bytes, 0, length, StandardCharsets.ISO_8859_1);
    }

    private static Map<String, List<String>> copyParams(Map<String, List<String>> params) {
        Map<String, List<String>> copied = new HashMap<>();

        params.forEach((key, values) -> copied.put(key, List.copyOf(values)));

        return Map.copyOf(copied);
    }

    public boolean isMatched(HttpMethod method, String path) {
        return this.method == method && this.path.equals(path);
    }

    public boolean isGet() {
        return method == HttpMethod.GET;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParamValue(String name) {
        return getFirstValue(queryParams, name);
    }

    public String getBodyParamValue(String name) {
        return getFirstValue(bodyParams, name);
    }

    private String getFirstValue(Map<String, List<String>> params, String name) {
        List<String> values = params.get(name);

        if (values == null || values.isEmpty()) {
            return null;
        }

        return values.getFirst();
    }
}
