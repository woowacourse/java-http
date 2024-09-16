package org.apache.coyote.http11.message.common;

import java.util.Arrays;

public enum HttpHeader {
    HOST("Host"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    ACCEPT("Accept"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    CACHE_CONTROL("Cache-Control"),
    CONNECTION("Connection"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    PRAGMA("Pragma"),
    DATE("Date"),
    SEC_CH_UA("sec-ch-ua"),
    SEC_CH_UA_MOBILE("sec-ch-ua-mobile"),
    SEC_CH_UA_PLATFORM("sec-ch-ua-platform"),
    SEC_FETCH_DEST("Sec-Fetch-Dest"),
    SEC_FETCH_MODE("Sec-Fetch-Mode"),
    SEC_FETCH_USER("Sec-Fetch-User"),
    SEC_FETCH_SITE("Sec-Fetch-Site"),
    UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests"),
    USER_AGENT("User-Agent"),
    LOCATION("Location"),
    REFERER("Referer"),
    ORIGIN("Origin");

    private final String displayName;

    HttpHeader(String displayName) {
        this.displayName = displayName;
    }

    public static HttpHeader from(String displayName) {
        return Arrays.stream(values())
                .filter(httpHeader -> httpHeader.displayName.equals(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(displayName + " 이름의 Header가 존재하지 않습니다."));
    }

    public String getDisplayName() {
        return displayName;
    }
}
