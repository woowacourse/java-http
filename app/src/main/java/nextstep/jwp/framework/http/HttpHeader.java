package nextstep.jwp.framework.http;

import java.util.HashMap;
import java.util.Map;

public enum HttpHeader {

    // General Header
    CONNECTION("Connection"),
    DATE("Date"),

    // Request Header
    AUTHORIZATION("Authorization"),
    COOKIE("Cookie"),
    USER_AGENT("User-Agent"),
    HOST("Host"),
    REFERER("Referer"),
    ORIGIN("Origin"),

    // Entity Header,
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),

    // Response Header,
    LOCATION("Location"),
    SERVER("Server"),
    SET_COOKIE("Set-Cookie");

    private static final Map<String, String> HEADER_MAP = new HashMap<>();

    static {
        for (HttpHeader httpHeader : values()) {
            HEADER_MAP.put(httpHeader.name(), httpHeader.headerName);
        }
    }

    private final String headerName;

    HttpHeader(String headerName) {
        this.headerName = headerName;
    }

    public static String resolve(String headerName) {
        return HEADER_MAP.getOrDefault(headerName.toUpperCase(), headerName);
    }

    public String getHeaderName() {
        return headerName;
    }
}
