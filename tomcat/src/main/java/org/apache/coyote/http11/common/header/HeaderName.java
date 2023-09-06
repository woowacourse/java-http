package org.apache.coyote.http11.common.header;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum HeaderName {

    /**
     * General headers
     */
    CACHE_CONTROL("Cache-Control"),
    CONNECTION("Connection"),
    DATE("Date"),
    PRAGMA("Pragma"),
    TRAILER("Trailer"),
    TRANSFER("Transfer"),
    UPGRADE("Upgrade"),
    VIA("Via"),
    WARNING("Warning"),

    /**
     * Request headers
     */
    ACCEPT("Accept"),
    ACCEPT_CHARSET("Accept-Charset"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    AUTHORIZATION("Authorization"),
    COOKIE("Cookie"),
    SECTION("Section"),
    EXPECT("Expected"),
    FROM("From"),
    HOST("Host"),
    IF_MATCH("If-Match"),

    /**
     * Response headers
     */
    ACCEPT_RANGES("Accept-Ranges"),
    AGE("Age"),
    ETAG("Etag"),
    LOCATION("Location"),
    PROXY_AUTHENTICATE("Proxy-Authenticate"),
    RETRY_AFTER("Retry-After"),
    SERVER("Server"),
    SET_COOKIE("Set-Cookie"),
    VARY("Vary"),
    WWW_AUTHENTICATE("WWW-Authenticate"),

    /**
     * Entity header
     */
    CONTENT_ENCODING("Content-Encoding"),
    CONTENT_LANGUAGE("Content-Language"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_LOCATION("Content-Location"),
    CONTENT_MD5("Content-MD5"),
    CONTENT_RANGE("Content-Range"),
    CONTENT_TYPE("Content-Type"),
    EXPIRES("Expires"),
    LAST_MODIFIED("Last-Modified");

    private static final List<HeaderName> GENERAL_HEADERS = List.of(
            CACHE_CONTROL,
            CONNECTION,
            DATE,
            PRAGMA,
            TRAILER,
            TRANSFER,
            UPGRADE,
            VIA,
            WARNING
    );
    private static final List<HeaderName> REQUEST_HEADERS = List.of(
            ACCEPT,
            ACCEPT_CHARSET,
            ACCEPT_ENCODING,
            ACCEPT_LANGUAGE,
            AUTHORIZATION,
            COOKIE,
            SECTION,
            EXPECT,
            FROM,
            HOST,
            IF_MATCH
    );
    private static final List<HeaderName> RESPONSE_HEADERS = List.of(
            ACCEPT_RANGES,
            AGE,
            ETAG,
            LOCATION,
            PROXY_AUTHENTICATE,
            RETRY_AFTER,
            SERVER,
            SET_COOKIE,
            VARY,
            WWW_AUTHENTICATE
    );
    private static final List<HeaderName> ENTITY_HEADERS = List.of(
            CONTENT_ENCODING,
            CONTENT_LANGUAGE,
            CONTENT_LENGTH,
            CONTENT_LOCATION,
            CONTENT_MD5,
            CONTENT_RANGE,
            CONTENT_TYPE,
            EXPIRES,
            LAST_MODIFIED
    );

    private final String value;

    HeaderName(final String value) {
        this.value = value;
    }

    public static boolean isGeneralHeaders(final HeaderName headerName) {
        return GENERAL_HEADERS.contains(headerName);
    }

    public static boolean isRequestHeaders(final HeaderName headerName) {
        return REQUEST_HEADERS.contains(headerName);
    }

    public static boolean isResponseHeaders(final HeaderName headerName) {
        return RESPONSE_HEADERS.contains(headerName);
    }

    public static boolean isEntityHeaders(final HeaderName headerName) {
        return ENTITY_HEADERS.contains(headerName);
    }

    public static boolean isNotDefined(final HeaderName headerName) {
        return Arrays.stream(values())
                .noneMatch(value -> Objects.equals(value, headerName));
    }

    public static HeaderName find(final String headerName) {
        return Arrays.stream(values())
                .filter(value -> Objects.equals(value.getName(), headerName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("invalid header name"));
    }

    public String getName() {
        return value;
    }

}
