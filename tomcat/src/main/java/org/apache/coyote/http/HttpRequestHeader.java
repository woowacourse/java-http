package org.apache.coyote.http;

import static org.apache.coyote.http.HttpConstants.CRLF;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HttpRequestHeader {

    public static final int HTTP_REQUEST_LINE_INDEX = 0;
    public static final String COOKIE_SEPARATOR = ";";
    public static final String COOKIE_HEADER_NAME = "cookie";

    private final HttpRequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> cookies;

    public static HttpRequestHeader from(final String rawHeader) {
        if (rawHeader == null || rawHeader.trim().isEmpty()) {
            throw new IllegalArgumentException("HTTP 헤더는 null이거나 비어있을 수 없습니다");
        }

        final String[] headerLines = rawHeader.split(CRLF);

        final HttpRequestLine requestLine = HttpRequestLine.from(headerLines[HTTP_REQUEST_LINE_INDEX]);
        final Map<String, String> headers = parseHeaders(headerLines);
        final Map<String, String> cookies = parseCookies(headers.get(COOKIE_HEADER_NAME));

        return new HttpRequestHeader(requestLine, headers, cookies);
    }

    private static Map<String, String> parseHeaders(final String[] rawHeaders) {
        final Map<String, String> headers = new HashMap<>();

        for (int i = 1; i < rawHeaders.length; i++) {
            final String line = rawHeaders[i].trim();
            if (line.isEmpty()) {
                break;
            }
            parseHeaderLine(line, headers);
        }

        return headers;
    }

    private static void parseHeaderLine(final String rawHeader, final Map<String, String> headers) {
        final int colonIndex = rawHeader.indexOf(HttpConstants.HEADER_VALUE_SEPARATOR);
        if (colonIndex <= 0) {
            return;
        }
        final String headerName = rawHeader.substring(0, colonIndex).trim().toLowerCase();
        final String headerValue = rawHeader.substring(colonIndex + 1).trim();
        headers.put(headerName, headerValue);
    }

    private static Map<String, String> parseCookies(final String rawCookies) {
        final Map<String, String> cookies = new HashMap<>();

        if (rawCookies == null || rawCookies.isEmpty()) {
            return cookies;
        }

        final String[] pairs = rawCookies.split(COOKIE_SEPARATOR);
        for (final String pair : pairs) {
            parseCookieLine(pair, cookies);
        }

        return cookies;
    }

    private static void parseCookieLine(final String pair, final Map<String, String> cookies) {
        final int equalIndex = pair.indexOf(HttpConstants.KEY_VALUE_SEPARATOR);
        if (equalIndex != -1) {
            final String key = pair.substring(0, equalIndex).trim();
            final String value = pair.substring(equalIndex + 1).trim();
            cookies.put(key, value);
        }
    }

    public int getContentLength() {
        final String contentLengthValue = headers.get(HttpConstants.CONTENT_LENGTH_HEADER_NAME);
        if (contentLengthValue == null) {
            return 0;
        }

        try {
            return Integer.parseInt(contentLengthValue.trim());
        } catch (final NumberFormatException e) {
            return 0;
        }
    }

    public String getQueryParam(final String name) {
        return requestLine.getQueryParam(name);
    }

    public String getHeader(final String name) {
        return headers.getOrDefault(name.toLowerCase(), "");
    }

    public String getCookie(final String name) {
        return cookies.getOrDefault(name, "");
    }

    public ContentType getContentType() {
        return ContentType.valueOf(getHeader(ContentType.HEADER_NAME));
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
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

        sb.append(requestLine).append(CRLF);

        headers.forEach((name, value) ->
                sb.append(capitalizeFirstLetter(name))
                        .append(HttpConstants.HEADER_VALUE_SEPARATOR).append(" ")
                        .append(value).append(CRLF));

        return sb.toString();
    }
}
