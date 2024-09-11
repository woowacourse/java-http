package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.StringJoiner;

public record HttpHeader(Map<String, String> headers) {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSSESSIONID";
    private static final String HEADER_DELIMITER = ": ";
    private static final String MULTIPLE_HEADER_KEY_DELIMITER = ",";
    private static final String MULTIPLE_COOKIE_DELIMITER = "; ";
    private static final String COOKIE_KEY_VALUE_DELIMITER = "=";

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
        if (!headers.containsKey(CONTENT_LENGTH)) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(Integer.parseInt(headers.get(CONTENT_LENGTH)));
    }

    public void add(String key, String value) {
        headers.compute(key,
                (k, prev) -> prev == null ? value : String.join(MULTIPLE_HEADER_KEY_DELIMITER, prev, value));
    }

    public void setContentLength(int length) {
        headers.put(CONTENT_LENGTH, String.valueOf(length));
    }

    public void setContentType(String contentType) {
        headers.put(CONTENT_TYPE, contentType);
    }

    public void addSetCookie(String key, String value) {
        String newCookie = String.join(COOKIE_KEY_VALUE_DELIMITER, key, value);
        headers.compute(SET_COOKIE,
                (k, prev) -> prev == null ? newCookie : String.join(MULTIPLE_COOKIE_DELIMITER, prev, newCookie));
    }

    public Optional<String> getSessionId() {
        return Optional.ofNullable(headers.get(JSESSIONID));
    }

    public String toString() {
        StringJoiner joiner = new StringJoiner(Constants.CRLF);
        headers.forEach((key, value) -> joiner.add(String.join("", key, HEADER_DELIMITER, value)));
        return joiner.toString();
    }
}
