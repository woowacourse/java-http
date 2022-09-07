package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum HeaderKeys {

    ACCEPT("Accept"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    CACHE_CONTROL("Cache-Control"),
    CONNECTION("Connection"),
    COOKIE("Cookie"),
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

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String name;

    HeaderKeys(final String name) {
        this.name = name;
    }

    public static HeaderKeys from(final String headerName) {
        return Arrays.stream(values())
            .filter(value -> value.name.equals(headerName))
            .findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException(String.format("정의되지 않은 Header key 입니다. [%s]", headerName)));
    }

    public String getName() {
        return name;
    }
}
