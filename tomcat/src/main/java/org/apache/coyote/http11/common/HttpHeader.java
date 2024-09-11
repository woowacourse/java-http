package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.StringJoiner;

public record HttpHeader(Map<String, String> headers) {

    private static final String HEADER_DELIMITER = ": ";
    private static final String MULTIPLE_HEADER_KEY_DELIMITER = ",";
    private static final String MULTIPLE_COOKIE_DELIMITER = "; ";
    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    public static HttpHeader empty() {
        return new HttpHeader(new HashMap<>());
    }

    public static HttpHeader from(String rawHeaders) {
        Map<String, String> headers = new HeaderNameValuePairs(rawHeaders).getAll();
        return new HttpHeader(headers);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(headers.get(key));
    }

    public OptionalInt getContentLength() {
        if (!headers.containsKey(StandardHttpHeaderName.CONTENT_LENGTH.getName())) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(Integer.parseInt(headers.get(StandardHttpHeaderName.CONTENT_LENGTH.getName())));
    }

    public void add(String key, String value) {
        headers.compute(key,
                (k, prev) -> prev == null ? value : String.join(MULTIPLE_HEADER_KEY_DELIMITER, prev, value));
    }

    public void setContentLength(int length) {
        headers.put(StandardHttpHeaderName.CONTENT_LENGTH.getName(), String.valueOf(length));
    }

    public void setContentType(String contentType) {
        headers.put(StandardHttpHeaderName.CONTENT_TYPE.getName(), contentType);
    }

    public Optional<String> getCookie(String cookieName) {
        return Optional.ofNullable(headers.get(StandardHttpHeaderName.COOKIE.getName()))
                .map(CookieNameValuePairs::new)
                .flatMap(cookiePairs -> cookiePairs.get(cookieName));
    }

    public void addSetCookie(String key, String value) {
        String newCookie = String.join(COOKIE_KEY_VALUE_DELIMITER, key, value);
        headers.compute(StandardHttpHeaderName.SET_COOKIE.getName(),
                (k, prev) -> prev == null ? newCookie : String.join(MULTIPLE_COOKIE_DELIMITER, prev, newCookie));
    }

    public Optional<String> getSessionId() {
        return getCookie(SESSION_COOKIE_NAME);
    }

    public String toString() {
        StringJoiner joiner = new StringJoiner(Constants.CRLF);
        headers.forEach((key, value) -> joiner.add(String.join("", key, HEADER_DELIMITER, value)));
        return joiner.toString();
    }
}
