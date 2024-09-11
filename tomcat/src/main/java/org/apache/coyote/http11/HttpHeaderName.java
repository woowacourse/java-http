package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.util.Arrays;

public enum HttpHeaderName {

    ACCEPT("Accept"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    AUTHORIZATION("Authorization"),
    CACHE_CONTROL("Cache-Control"),
    CONNECTION("Connection"),
    CONTENT_ENCODING("Content-Encoding"),
    CONTENT_LANGUAGE("Content-Language"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    EXPIRES("Expires"),
    HOST("Host"),
    IF_MODIFIED_SINCE("If-Modified-Since"),
    LAST_MODIFIED("Last-Modified"),
    LOCATION("Location"),
    ORIGIN("Origin"),
    PRAGMA("Pragma"),
    REFERER("Referer"),
    SEC_CH_UA("Sec-CH-UA"),
    SEC_CH_UA_MOBILE("Sec-CH-UA-Mobile"),
    SEC_CH_UA_PLATFORM("Sec-CH-UA-Platform"),
    SEC_FETCH_DEST("Sec-Fetch-Dest"),
    SEC_FETCH_MODE("Sec-Fetch-Mode"),
    SEC_FETCH_SITE("Sec-Fetch-Site"),
    SEC_FETCH_USER("Sec-Fetch-User"),
    SET_COOKIE("Set-Cookie"),
    UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests"),
    USER_AGENT("User-Agent"),
    VARY("Vary"),
    X_CONTENT_TYPE_OPTIONS("X-Content-Type-Options"),
    X_FRAME_OPTIONS("X-Frame-Options"),
    X_XSS_PROTECTION("X-XSS-Protection");

    private final String value;

    HttpHeaderName(String value) {
        this.value = value;
    }

    public static HttpHeaderName from(String target) {
        return Arrays.stream(values())
                .filter(httpHeaderName -> httpHeaderName.value.equalsIgnoreCase(target))
                .findAny()
                .orElseThrow(() -> new UncheckedServletException("정의 되지 않은 헤더 값입니다: " + target));
    }

    public String getValue() {
        return value;
    }
}
