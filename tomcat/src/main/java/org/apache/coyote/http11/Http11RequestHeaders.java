package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class Http11RequestHeaders {

    private static final String HEADER_DELIMITER = ": ";
    private static final int HEADER_LENGTH = 2;

    private final Map<String, String> headers;
    private final HttpCookie cookie;

    public Http11RequestHeaders(List<String> headers) {
        this.headers = new LinkedHashMap<>();
        this.cookie = new HttpCookie();
        for (String header : headers) {
            validateHeader(header);
            String key = header.split(HEADER_DELIMITER)[0];
            String value = header.split(HEADER_DELIMITER)[1];
            append(key, value);
        }
    }

    private void append(String key, String value) {
        if ("Cookie".equals(key)) {
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

    public boolean existsCookie(String key) {
        return cookie.isExists(key);
    }
}
