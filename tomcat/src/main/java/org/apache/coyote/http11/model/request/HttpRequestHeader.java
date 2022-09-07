package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.exception.InvalidHttpRequestException;
import org.apache.coyote.http11.model.HttpCookie;
import org.apache.coyote.http11.model.HttpHeaderType;

public class HttpRequestHeader {

    private static final String KEY_VALUE_SEPARATOR = ": ";
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private final Map<String, String> values;
    private final HttpCookie httpCookie;

    private HttpRequestHeader(Map<String, String> values, HttpCookie httpCookie) {
        this.values = values;
        this.httpCookie = httpCookie;
    }

    public static HttpRequestHeader from(List<String> headerValues) {
        Map<String, String> headers = parseHeaderValues(headerValues);
        if (headers.containsKey(HttpHeaderType.COOKIE)) {
            String cookiesBeforeParsed = headers.remove(HttpHeaderType.COOKIE);
            HttpCookie cookie = HttpCookie.of(cookiesBeforeParsed);
            return new HttpRequestHeader(headers, cookie);
        }
        return new HttpRequestHeader(headers, HttpCookie.empty());
    }

    private static Map<String, String> parseHeaderValues(List<String> headerValues) {
        validateEmptyHeader(headerValues);
        Map<String, String> values = new HashMap<>();
        for (String headerValue : headerValues) {
            String[] keyAndValue = headerValue.split(KEY_VALUE_SEPARATOR);
            values.put(keyAndValue[KEY], keyAndValue[VALUE]);
        }
        return values;
    }

    private static void validateEmptyHeader(List<String> headerValues) {
        if (headerValues.isEmpty()) {
            throw new InvalidHttpRequestException("Http Request Header가 비어있습니다.");
        }
    }

    public String getHeaderValue(String key) {
        return values.get(key);
    }

    public boolean hasNotHeader(String key) {
        return !values.containsKey(key);
    }

    public String getCookieValue(String key) {
        return httpCookie.getValue(key);
    }

    public boolean hasCookie() {
        return !httpCookie.isEmpty();
    }
}
