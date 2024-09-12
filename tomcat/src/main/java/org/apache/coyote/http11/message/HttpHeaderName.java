package org.apache.coyote.http11.message;

import java.util.Arrays;

public enum HttpHeaderName {

    ACCEPT("Accept"),
    ACCEPT_CHARSET("Accept-Charset"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    AUTHORIZATION("Authorization"),
    CACHE_CONTROL("Cache-Control"),
    CONNECTION("Connection"),
    COOKIE("Cookie"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    HOST("Host"),
    IF_MODIFIED_SINCE("If-Modified-Since"),
    IF_NONE_MATCH("If-None-Match"),
    ORIGIN("Origin"),
    PRAGMA("Pragma"),
    REFERER("Referer"),
    SET_FETCH_DEST("Sec-Fetch-Dest"),
    SEC_FETCH_MODE("Sec-Fetch-Mode"),
    SEC_FETCH_SITE("Sec-Fetch-Site"),
    SEC_FETCH_USER("Sec-Fetch-User"),
    SEC_CH_UA("sec-ch-ua"),
    SEC_CH_UA_MOBILE("sec-ch-ua-mobile"),
    SEC_CH_UA_PLATFORM("sec-ch-ua-platform"),
    USER_AGENT("User-Agent"),
    UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests"),
    ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),
    ACCEPT_RANGES("Accept-Ranges"),
    AGE("Age"),
    ALLOW("Allow"),
    CONTENT_DISPOSITION("Content-Disposition"),
    CONTENT_ENCODING("Content-Encoding"),
    CONTENT_LANGUAGE("Content-Language"),
    CONTENT_LOCATION("Content-Location"),
    CONTENT_RANGE("Content-Range"),
    DATE("Date"),
    ETAG("ETag"),
    EXPIRES("Expires"),
    LAST_MODIFIED("Last-Modified"),
    LINK("Link"),
    LOCATION("Location"),
    RETRY_AFTER("Retry-After"),
    SERVER("Server"),
    SET_COOKIE("Set-Cookie"),
    TRANSFER_ENCODING("Transfer-Encoding"),
    VARY("Vary"),
    WWW_AUTHENTICATE("WWW-Authenticate"),
    UPGRADE("Upgrade"),
    VIA("Via"),
    WARNING("Warning");

    private final String name;

    HttpHeaderName(String name) {
        this.name = name;
    }

    public static HttpHeaderName from(String name) {
        return Arrays.stream(values())
                .filter(headerName -> name.equals(headerName.name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(name + "라는 이름의 헤더가 없습니다."));
    }

    public String getName() {
        return name;
    }
}

