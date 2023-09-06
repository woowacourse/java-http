package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpHeaders {

    public static final String HEADER_DELIMITER = ": ";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_UTF_8 = "text/html;charset=utf-8";
    public static final String CONTENT_TYPE_CSS = "text/css";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String ACCEPT = "Accept";
    public static final String LOCATION = "Location";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String HTTP_LINE_SUFFIX = "\r\n";
    private static final String CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String COOKIE = "Cookie";
    private static final String DEFAULT_CONTENT_LENGTH = "0";
    private static final String COOKIE_KEY_VALUE_DEMLIMITER = "=";

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders defaultHeaders() {
        return new HttpHeaders(new HashMap<>());
    }

    public static HttpHeaders createBasicRequestHeadersFrom(List<String> request) {
        Map<String, String> headers = request.stream()
                                             .skip(1)
                                             .takeWhile(line -> !line.isEmpty())
                                             .map(line -> line.split(HEADER_DELIMITER))
                                             .collect(Collectors.toMap(line -> line[0].trim(), line -> line[1].trim()));
        return new HttpHeaders(headers);
    }

    public static HttpHeaders createBasicResponseHeadersFrom(HttpRequest request) {
        final var headers = new HashMap<String, String>();
        headers.put(CONTENT_TYPE, getContentTypeFrom(request.getHeaders().get(ACCEPT)));
        headers.put(CONTENT_LENGTH, DEFAULT_CONTENT_LENGTH);
        return new HttpHeaders(headers);
    }

    private static String getContentTypeFrom(String accept) {
        if (accept == null) {
            return CONTENT_TYPE_UTF_8;
        }
        if (accept.contains(CONTENT_TYPE_CSS)) {
            return CONTENT_TYPE_CSS;
        }
        return CONTENT_TYPE_UTF_8;
    }

    public boolean isContentTypeUrlEncoded() {
        return headers.get(CONTENT_TYPE).contains(CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED);
    }

    public boolean hasCookie(String key) {
        return headers.containsKey(COOKIE) && headers.get(COOKIE).contains(key);
    }

    private String get(String key) {
        return headers.get(key);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault(CONTENT_LENGTH, DEFAULT_CONTENT_LENGTH));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Optional<String> getCookie(String key) {
        final String cookies = headers.get(COOKIE);
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies.split(";"))
                     .filter(cookie -> cookie.contains(key))
                     .map(cookie -> Optional.of(cookie.split(COOKIE_KEY_VALUE_DEMLIMITER)[1]))
                     .findAny()
                     .orElseGet(Optional::empty);
    }

    public void put(String key, String value) {
        if (key.equals(SET_COOKIE)) {
            setCookie(
                    value.split(COOKIE_KEY_VALUE_DEMLIMITER)[0],
                    value.split(COOKIE_KEY_VALUE_DEMLIMITER)[1]
            );
            return;
        }
        headers.put(key, value);
    }

    public void setCookie(String key, String value) {
        if (headers.containsKey(SET_COOKIE)) {
            headers.put(SET_COOKIE, headers.get(SET_COOKIE) + "; " + key + COOKIE_KEY_VALUE_DEMLIMITER + value);
            return;
        }
        headers.put(SET_COOKIE, key + COOKIE_KEY_VALUE_DEMLIMITER + value);
    }

    @Override
    public String toString() {
        return headers.entrySet().stream()
                      .map(entry -> entry.getKey() + HEADER_DELIMITER + entry.getValue())
                      .collect(Collectors.joining(HTTP_LINE_SUFFIX, "", HTTP_LINE_SUFFIX));
    }
}
