package org.apache.http.header;

import java.util.Arrays;

public enum HttpHeaderName {
    ACCEPT("Accept"),
    ACCEPT_RANGES("Accept-Ranges"),
    AUTHORIZATION("Authorization"),
    CONNECTION("Connection"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    HOST("Host"),
    LOCATION("Location"),
    ORIGIN("Origin"),
    SET_COOKIE("Set-Cookie"),
    CACHE_CONTROL("Cache-Control"),
    PRAGMA("Pragma"),
    USER_AGENT("User-Agent"),
    REFERER("Referer"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    SEC_FETCH_SITE("Sec-Fetch-Site"),
    SEC_FETCH_MODE("Sec-Fetch-Mode"),
    SEC_CH_UA("Sec-CH-UA"),
    SEC_CH_UA_MOBILE("Sec-CH-UA-Mobile"),
    SEC_FETCH_USER("Sec-Fetch-User"),
    SEC_FETCH_DEST("Sec-Fetch-Dest"),
    SEC_CH_UA_PLATFORM("Sec-CH-UA-Platform"),
    SEC_CH_UA_ARCH("Sec-CH-UA-Arch"),
    UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests"),
    ;

    private final String value;

    HttpHeaderName(String value) {
        this.value = value;
    }

    public static HttpHeaderName from(String value) {
        return Arrays.stream(values())
                .filter(httpHeaderName -> httpHeaderName.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 HttpHeaderName이 없습니다."));
    }

    public boolean equalsIgnoreCase(String value) {
        return this.value.equalsIgnoreCase(value);
    }

    public String getValue() {
        return value;
    }
}
