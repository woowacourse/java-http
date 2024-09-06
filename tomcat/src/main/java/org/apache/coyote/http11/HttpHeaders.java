package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.exception.UncheckedHttpException;

public enum HttpHeaders {

    HOST("Host"),
    USER_AGENT("User-Agent"),
    ACCEPT("Accept"),
    ACCEPT_LANGUAGE("Accept-Language"),
    ACCEPT_ENCODING("Accept-Encoding"),
    CONNECTION("Connection"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    ORIGIN("Origin"),
    REFERER("Referer"),
    COOKIE("Cookie"),
    SEC_FETCH_SITE("Sec-Fetch-Site"),
    SEC_FETCH_MODE("Sec-Fetch-Mode"),
    SEC_FETCH_DEST("Sec-Fetch-Dest"),
    SEC_FETCH_USER("Sec-Fetch-User"),
    UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests"),
    SEC_CH_UA("sec-ch-ua"),
    SEC_CH_UA_MOBILE("sec-ch-ua-mobile"),
    SEC_CH_UA_PLATFORM("sec-ch-ua-platform"),
    AUTHORIZATION("Authorization"),
    PROXY_AUTHORIZATION("Proxy-Authorization"),
    IF_MODIFIED_SINCE("If-Modified-Since"),
    IF_NONE_MATCH("If-None-Match"),
    IF_MATCH("If-Match"),
    IF_UNMODIFIED_SINCE("If-Unmodified-Since"),
    EXPECT("Expect"),
    TE("TE"),
    RANGE("Range"),
    PRAGMA("Pragma"),
    CACHE_CONTROL("Cache-Control"),

    SERVER("Server"),
    DATE("Date"),
    CONTENT_ENCODING("Content-Encoding"),
    CONTENT_LANGUAGE("Content-Language"),
    CONTENT_LOCATION("Content-Location"),
    CONTENT_DISPOSITION("Content-Disposition"),
    LAST_MODIFIED("Last-Modified"),
    ETAG("ETag"),
    ACCEPT_RANGES("Accept-Ranges"),
    AGE("Age"),
    ALLOW("Allow"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    EXPIRES("Expires"),
    VARY("Vary"),
    WARNING("Warning"),
    WWW_AUTHENTICATE("WWW-Authenticate"),
    PROXY_AUTHENTICATE("Proxy-Authenticate"),
    FORWARDED("Forwarded"),
    STRICT_TRANSPORT_SECURITY("Strict-Transport-Security"),
    TRANSFER_ENCODING("Transfer-Encoding"),
    TRAILER("Trailer"),
    UPGRADE("Upgrade"),
    X_CONTENT_TYPE_OPTIONS("X-Content-Type-Options"),
    X_FRAME_OPTIONS("X-Frame-Options"),
    X_XSS_PROTECTION("X-XSS-Protection"),
    CONTENT_SECURITY_POLICY("Content-Security-Policy");

    private final String header;

    HttpHeaders(String header) {
        this.header = header;
    }

    public static HttpHeaders from(String value) {
        return Arrays.stream(values())
                .filter(httpHeader -> httpHeader.header.equals(value))
                .findFirst()
                .orElseThrow(() -> new UncheckedHttpException(new IllegalArgumentException("존재하지 않는 헤더입니다.")));
    }

    public String getHeader() {
        return header;
    }
}
