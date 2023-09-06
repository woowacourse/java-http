package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpHeader {

    ACCEPT("Accept"),
    ACCEPT_ECCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    CONNECTION("Connection"),
    CACHE_CONTROL("Cache-Control"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    DNT("DNT"),
    HOST("Host"),
    LOCATION("Location"),
    ORIGIN("Origin"),
    PRAGMA("Pragma"),
    REFERER("Referer"),
    SEC_CH_UA("sec-ch-ua"),
    SEC_CH_UA_MOBILE("sec-ch-ua-mobile"),
    SEC_CH_UA_PLATFORM("sec-ch-ua-platform"),
    SEC_FETCH_SITE("Sec-Fetch-Site"),
    SEC_FETCH_DEST("Sec-Fetch-Dest"),
    SEC_FETCH_MODE("Sec-Fetch-Mode"),
    SEC_FETCH_USER("Sec-Fetch-User"),
    SET_COOKIE("Set-Cookie"),
    UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests"),
    USER_AGENT("User-Agent"),
    ;

    private final String value;

    HttpHeader(final String value) {
        this.value = value;
    }

    public static HttpHeader from(final String target) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.getValue().equalsIgnoreCase(target))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Header 입니다."));
    }

    public String getValue() {
        return value;
    }
}
