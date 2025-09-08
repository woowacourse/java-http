package org.apache.coyote.http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class HttpRequest {

    private static final String CRLF = "\r\n";
    private static final String HEADER_BODY_SEPARATOR = CRLF + CRLF;
    private static final String HTTP_PREFIX = "HTTP/";
    private static final String COOKIE_SEPARATOR = ";";
    private static final String PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String COOKIE_HEADER = "cookie";

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_PATH_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int MIN_REQUEST_LINE_PARTS = 3;

    private final HttpMethod method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final Map<String, String> requestBody;
    private final Map<String, String> cookies;

    public static HttpRequest from(final String rawRequest) {
        if (rawRequest == null || rawRequest.trim().isEmpty()) {
            throw new IllegalArgumentException("HTTP 요청은 null이거나 비어있을 수 없습니다");
        }

        final String[] parts = rawRequest.split(HEADER_BODY_SEPARATOR, 2);
        final String headerPart = parts[0];
        final String bodyPart = parts.length > 1 ? parts[1] : "";

        final String[] headerLines = headerPart.split(CRLF);
        if (headerLines.length == 0) {
            throw new IllegalArgumentException("HTTP 헤더는 비어있을 수 없습니다");
        }

        final String[] requestLineParts = parseFirstHeaderLine(headerLines[0]);

        final HttpMethod method = HttpMethod.from(requestLineParts[HTTP_METHOD_INDEX]);
        final String fullPath = requestLineParts[HTTP_PATH_INDEX];
        final String version = requestLineParts[HTTP_VERSION_INDEX].replace(HTTP_PREFIX, "");

        final String path = extractPath(fullPath);
        final Map<String, String> queryParams = extractQueryParams(fullPath);
        final Map<String, String> headers = parseHeaders(headerLines);

        final Map<String, String> requestBody = parseBody(bodyPart, headers.get(ContentType.HEADER_NAME));
        final Map<String, String> cookies = parseCookies(headers.get(COOKIE_HEADER));

        return new HttpRequest(method, path, version, headers, queryParams, requestBody, cookies);
    }

    private static String[] parseFirstHeaderLine(final String requestLine) {
        validateRequestLine(requestLine);

        final String[] parts = requestLine.trim().split("\\s+");
        validateRequestLineParts(parts);

        return parts;
    }

    private static void validateRequestLine(final String requestLine) {
        if (!requestLine.contains(HTTP_PREFIX)) {
            throw new IllegalArgumentException("HTTP 요청의 첫 번째 줄은 " + HTTP_PREFIX + "를 포함해야 합니다");
        }
    }

    private static void validateRequestLineParts(final String[] parts) {
        if (parts.length < MIN_REQUEST_LINE_PARTS) {
            throw new IllegalArgumentException(
                    "HTTP 요청의 첫 번째 줄은 " + MIN_REQUEST_LINE_PARTS + "개의 부분을 포함해야 합니다");
        }
    }

    private static String extractPath(final String fullPath) {
        final int queryIndex = fullPath.indexOf('?');
        return queryIndex != -1 ? fullPath.substring(0, queryIndex) : fullPath;
    }

    private static Map<String, String> extractQueryParams(final String fullPath) {
        final Map<String, String> queryParams = new HashMap<>();
        final int queryIndex = fullPath.indexOf('?');

        if (queryIndex != -1) {
            final String queryString = fullPath.substring(queryIndex + 1);
            parseQueryString(queryString, queryParams);
        }

        return queryParams;
    }

    private static void parseQueryString(final String queryString, final Map<String, String> queryParams) {
        if (queryString == null || queryString.isEmpty()) {
            return;
        }

        final String[] pairs = queryString.split(PARAM_SEPARATOR);
        for (final String pair : pairs) {
            parseKeyValuePair(pair, queryParams);
        }
    }

    private static void parseKeyValuePair(final String pair, final Map<String, String> params) {
        final int equalIndex = pair.indexOf(KEY_VALUE_SEPARATOR);
        if (equalIndex != -1) {
            final String key = URLDecoder.decode(pair.substring(0, equalIndex), StandardCharsets.UTF_8);
            final String value = URLDecoder.decode(pair.substring(equalIndex + 1), StandardCharsets.UTF_8);
            params.put(key, value);
        }
    }

    private static Map<String, String> parseHeaders(final String[] lines) {
        final Map<String, String> headers = new HashMap<>();

        for (int i = 1; i < lines.length; i++) {
            final String line = lines[i].trim();
            if (line.isEmpty()) {
                break;
            }
            parseHeaderLine(line, headers);
        }

        return headers;
    }

    private static void parseHeaderLine(final String line, final Map<String, String> headers) {
        final int colonIndex = line.indexOf(':');
        if (colonIndex <= 0) {
            return;
        }
        final String headerName = line.substring(0, colonIndex).trim().toLowerCase();
        final String headerValue = line.substring(colonIndex + 1).trim();
        headers.put(headerName, headerValue);
    }

    private static Map<String, String> parseBody(final String body, final String contentType) {
        final Map<String, String> map = new HashMap<>();

        if (isEmptyBody(body)) {
            return map;
        }

        if (isFormUrlEncoded(contentType)) {
            parseQueryString(body, map);
        }

        return map;
    }

    private static boolean isEmptyBody(final String body) {
        return body == null || body.isEmpty();
    }

    private static boolean isFormUrlEncoded(final String contentType) {
        final String type = contentType == null ? "" : contentType.toLowerCase();
        return type.contains(ContentType.FORM_URLENCODED.getMimeType());
    }

    private static Map<String, String> parseCookies(final String cookieHeader) {
        final Map<String, String> cookies = new HashMap<>();

        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return cookies;
        }

        final String[] pairs = cookieHeader.split(COOKIE_SEPARATOR);
        for (final String pair : pairs) {
            parseCookiePair(pair, cookies);
        }

        return cookies;
    }

    private static void parseCookiePair(final String pair, final Map<String, String> cookies) {
        final int equalIndex = pair.indexOf(KEY_VALUE_SEPARATOR);
        if (equalIndex != -1) {
            final String key = pair.substring(0, equalIndex).trim();
            final String value = pair.substring(equalIndex + 1).trim();
            cookies.put(key, value);
        }
    }

    public String getQueryParam(final String name) {
        return queryParams.getOrDefault(name, "");
    }

    public String getHeader(final String name) {
        return headers.getOrDefault(name.toLowerCase(), "");
    }

    public String getBodyParam(final String name) {
        return requestBody.getOrDefault(name, "");
    }

    public String getCookie(final String name) {
        return cookies.getOrDefault(name, "");
    }

    private void appendRequestLine(final StringBuilder sb) {
        final String protocol = "%s%s".formatted(HTTP_PREFIX, version);
        sb.append(String.format("%s %s %s %s", method, path, protocol, CRLF));
    }

    private void appendHeaders(final StringBuilder sb) {
        headers.forEach((key, value) ->
                sb.append(String.format("%s: %s%s", capitalizeFirstLetter(key), value, CRLF)));
    }

    private String capitalizeFirstLetter(final String str) {
        if (str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        appendRequestLine(sb);
        appendHeaders(sb);
        sb.append(CRLF);
        return sb.toString();
    }
}
