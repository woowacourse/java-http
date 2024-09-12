package org.apache.coyote.util;

import java.util.Arrays;

public enum HttpHeaders {

    ACCEPT("Accept"),
    ORIGIN("Origin"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    CONNECTION("Connection"),
    HOST("Host"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    CACHE_CONTROL("Cache-Control"),
    SEC_CH_UA("sec-ch-ua"),
    SEC_CH_UA_MOBILE("sec-ch-ua-mobile"),
    SEC_CH_UA_PLATFORM("sec-ch-ua-platform"),
    SEC_FETCH_DEST("Sec-Fetch-Dest"),
    SEC_FETCH_MODE("Sec-Fetch-Mode"),
    SEC_FETCH_SITE("Sec-Fetch-Site"),
    SEC_FETCH_USER("Sec-Fetch-User"),
    UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests"),
    USER_AGENT("User-Agent"),
    REFERER("Referer"),
    LOCATION("Location");

    private final String value;

    HttpHeaders(String value) {
        this.value = value;
    }

    public static HttpHeaders findHeader(String headerKey) {
        return Arrays.stream(HttpHeaders.values())
                .filter(httpHeader -> httpHeader.getValue().equals(headerKey))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 헤더입니다."));
    }

    public String getValue() {
        return value;
    }
}
