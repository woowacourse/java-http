package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpHeaderName {
    ACCEPT("Accept"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    CACHE_CONTROL("Cache-Control"),
    CONNECTION("Connection"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    DNT("Dnt"),
    HOST("Host"),
    ORIGIN("Origin"),
    PRAGMA("Pragma"),
    REFERER("Referer"),
    SEC_CH_UA("Sec-Ch-Ua"),
    SEC_CH_UA_MOBILE("Sec-Ch-Ua-Mobile"),
    SEC_CH_UA_PLATFORM("Sec-Ch-Ua-Platform"),
    SEC_FETCH_DEST("Sec-Fetch-Dest"),
    SEC_FETCH_MODE("Sec-Fetch-Mode"),
    SEC_FETCH_SITE("Sec-Fetch-Site"),
    SEC_FETCH_USER("Sec-Fetch-User"),
    UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests"),
    USER_AGENT("User-Agent"),
    LOCATION("Location"),
    ;

    private final String value;

    HttpHeaderName(final String value) {
        this.value = value;
    }

    public static HttpHeaderName getHeaderName(final String rawHeaderName) {
        return Arrays.stream(values())
            .filter(headerName -> isSameHeaderName(rawHeaderName, headerName))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("해당 이름을 가진 헤더가 존재하지 않습니다."));
    }

    public static boolean isSameHeaderName(final String rawHeaderName, final HttpHeaderName headerName) {
        final String lowerCaseHeaderName = headerName.value.toLowerCase();
        return lowerCaseHeaderName.equals(rawHeaderName.toLowerCase());
    }

    public String getValue() {
        return value;
    }
}
