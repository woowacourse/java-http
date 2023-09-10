package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.exception.BadRequestException;
import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.common.HttpCookies;

public class HttpRequestHeaders {

    private static final String COOKIE_HEADER_PREFIX = "Cookie: ";
    private static final String KET_VALUE_DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private final Map<String, String> headers;
    private final HttpCookies cookies;

    private HttpRequestHeaders(Map<String, String> headers, HttpCookies cookies) {
        this.headers = headers;
        this.cookies = cookies;
    }

    public static HttpRequestHeaders from(List<String> lines) {
        Map<String, String> headers = lines.stream()
                .filter(line -> !line.startsWith(COOKIE_HEADER_PREFIX))
                .map(line -> line.split(KET_VALUE_DELIMITER))
                .collect(Collectors.toMap(line -> line[KEY_INDEX], line -> line[VALUE_INDEX]));

        if (hasCookie(lines)) {
            String cookieLine = lines.stream()
                    .filter(line -> line.startsWith(COOKIE_HEADER_PREFIX))
                    .map(line -> line.replaceFirst(COOKIE_HEADER_PREFIX, ""))
                    .collect(Collectors.joining());

            return new HttpRequestHeaders(headers, HttpCookies.from(cookieLine));
        }

        return new HttpRequestHeaders(headers, HttpCookies.createEmptyCookies());
    }

    private static boolean hasCookie(List<String> lines) {
        return lines.stream()
                .anyMatch(line -> line.startsWith(COOKIE_HEADER_PREFIX));
    }

    public boolean hasCookie() {
        return !cookies.isEmpty();
    }

    public boolean containsKey(String key) {
        validateKey(key);

        return headers.containsKey(key);
    }

    private void validateKey(String key) {
        if (key == null) {
            throw new BadRequestException("Header Key is Null");
        }
    }

    public Cookie getCookie(String key) {
        validateKey(key);

        return cookies.get(key);
    }

    public String get(String key) {
        validateKey(key);

        return headers.get(key);
    }

}
