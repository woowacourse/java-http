package org.apache.coyote.http11.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public class HttpHeaders {

    public static final String COOKIE_NAME = "JSESSIONID";
    public static final String LOCATION = "LOCATION";

    private static final String DELIMITER = ": ";
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String EMPTY_LINE = "";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE_VALUE_DELIMITER = "=";
    private static final int COOKIE_NAME_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;
    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE = "Cookie";
    private static final String EMPTY = "";
    private static final String NEXT_LINE = " \r\n";
    private static final String COMMA_REGEX = "\\.";
    private static final String SIMPLE_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String SIMPLE_CONTENT_LENGTH = "12";

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders createSimpleText() {
        final Map<String, String> headers = new LinkedHashMap<>();

        headers.put(CONTENT_TYPE, SIMPLE_CONTENT_TYPE);
        headers.put(CONTENT_LENGTH, SIMPLE_CONTENT_LENGTH);

        return new HttpHeaders(headers);
    }

    public static HttpHeaders parse(final List<String> lines) {
        final Map<String, String> headers = new LinkedHashMap<>();

        for (final String line : lines) {
            final String[] splitLine = line.split(DELIMITER);

            final String key = splitLine[HEADER_NAME_INDEX];
            final String value = splitLine[HEADER_VALUE_INDEX];

            headers.put(key, value);
        }

        return new HttpHeaders(headers);
    }

    public static HttpHeaders createResponse(final Path path) throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();
        final byte[] bytes = Files.readAllBytes(path);

        headers.put(CONTENT_TYPE, findContentType(path));
        headers.put(CONTENT_LENGTH, String.valueOf(bytes.length));

        return new HttpHeaders(headers);
    }

    private static String findContentType(final Path path) {
        final String fileName = path.getFileName()
                .toString();
        final String[] splitFileName = fileName.split(COMMA_REGEX);

        return ContentType.findMimeType(splitFileName[1]);
    }

    public boolean hasContentLength() {
        return headers.containsKey(CONTENT_LENGTH);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }

    public void setHeader(final String key, final String value) {
        headers.put(key, value);
    }

    public String getCookie(final String key) {
        final String cookies = headers.get(COOKIE);
        if (Objects.isNull(cookies)) {
            return EMPTY;
        }

        final String[] splitCookies = cookies.split(HttpHeaders.COOKIE_DELIMITER);
        for (String cookie : splitCookies) {
            final String cookieName = cookie.split(COOKIE_VALUE_DELIMITER)[COOKIE_NAME_INDEX];
            final String cookieValue = cookie.split(COOKIE_VALUE_DELIMITER)[COOKIE_VALUE_INDEX];

            if (cookieName.equals(key)) {
                return cookieValue;
            }
        }

        return EMPTY;
    }

    public void setCookie(final String key, final String value) {
        headers.put(SET_COOKIE, key + COOKIE_VALUE_DELIMITER + value);
    }

    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner(NEXT_LINE);
        final Set<String> keys = headers.keySet();
        for (String key : keys) {
            joiner.add(key + DELIMITER + headers.get(key));
        }
        joiner.add(EMPTY_LINE);
        return joiner.toString();
    }
}
