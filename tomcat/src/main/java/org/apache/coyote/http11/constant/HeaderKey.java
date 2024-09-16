package org.apache.coyote.http11.constant;

import java.util.Arrays;

public enum HeaderKey {

    HOST("Host"),
    ACCEPT("Accept"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    CONNECTION("Connection"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    CACHE_CONTROL("Cache-Control"),
    SEC_FETCH_DEST("Sec-Fetch-Dest"),
    SEC_FETCH_MODE("Sec-Fetch-Mode"),
    SEC_FETCH_SITE("Sec-Fetch-Site"),
    SEC_FETCH_USER("Sec-Fetch-User"),
    SEC_CH_UA("sec-ch-ua"),
    SEC_CH_UA_MOBILE("sec-ch-ua-mobile"),
    SEC_CH_UA_PLATFORM("sec-ch-ua-platform"),
    UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests"),
    USER_AGENT("User-Agent"),
    REFERER("Referer"),
    ORIGIN("Origin"),
    ;

    private final String value;

    HeaderKey(String value) {
        this.value = value;
    }

    public static HeaderKey find(String rawKey) {
        return Arrays.stream(values())
                .filter(value -> value.value.equals(rawKey))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 헤더입니다."));
    }

    public String getValue() {
        return this.value;
    }
}
