package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaderName;

public class Http11RequestHeaders {

    private static final String HEADER_DELIMITER = ": ";
    private static final String ACCEPT_HEADER_DELIMITER = ",";
    private static final int HEADER_LENGTH = 2;
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<String, String> headers;
    private final HttpCookie cookie;

    public Http11RequestHeaders(List<String> headers) {
        this.headers = new LinkedHashMap<>();
        this.cookie = new HttpCookie();
        for (String header : headers) {
            validateHeader(header);
            String key = header.split(HEADER_DELIMITER)[HEADER_NAME_INDEX];
            String value = header.split(HEADER_DELIMITER)[HEADER_VALUE_INDEX];
            append(key, value);
        }
    }

    private void append(String key, String value) {
        if (HttpHeaderName.COOKIE.equalsName(key)) {
            cookie.setCookie(value);
            return;
        }
        this.headers.put(key, value);
    }

    private void validateHeader(String header) {
        if (StringUtils.isBlank(header) || header.split(HEADER_DELIMITER).length != HEADER_LENGTH) {
            throw new IllegalArgumentException("잘못된 헤더 형식입니다.");
        }
    }

    public String getValue(String key) {
        return headers.get(key);
    }

    public String getCookie(String key) {
        return cookie.getValue(key);
    }

    public String getSession() {
        return cookie.getSession();
    }

    public boolean existsSession() {
        return cookie.existsSession();
    }

    public String getContentLength() {
        return headers.get(HttpHeaderName.CONTENT_LENGTH.getName());
    }

    public boolean existsCookie(String key) {
        return cookie.isExists(key);
    }

    public boolean existsAccept() {
        return headers.containsKey(HttpHeaderName.ACCEPT.getName());
    }

    public String getFirstAcceptMimeType() {
        return Arrays.stream(getValue(HttpHeaderName.ACCEPT.getName()).split(ACCEPT_HEADER_DELIMITER))
                .toList()
                .getFirst();
    }
}
