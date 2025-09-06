package org.apache.coyote.response.responseHeader;

import java.util.Arrays;

public enum HttpHeaders {

    ACCEPT("Accept"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    CACHE_CONTROL("Cache-Control"),
    CONNECTION("Connection"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    HOST("Host"),
    ORIGIN("Origin"),
    REFERER("Referer"),

    SEC_CH_UA("sec-ch-ua"),
    SEC_CH_UA_MOBILE("sec-ch-ua-mobile"),
    SEC_CH_UA_PLATFORM("sec-ch-ua-platform"),
    SEC_FETCH_DEST("Sec-Fetch-Dest"),
    SEC_FETCH_MODE("Sec-Fetch-Mode"),
    SEC_FETCH_SITE("Sec-Fetch-Site"),
    SEC_FETCH_USER("Sec-Fetch-User"),

    UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests"),
    USER_AGENT("User-Agent"),

    PRAGMA("Pragma"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie")
    ;

    private final String value;

    HttpHeaders(String value) {
        this.value = value;
    }

    public static HttpHeaders find(String headerKey) {
        return Arrays.stream(HttpHeaders.values())
                .filter(httpHeaders -> httpHeaders.value.equals(headerKey))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("[ERROR] 잘못된 헤더입니다: %s", headerKey)));
    }
}
