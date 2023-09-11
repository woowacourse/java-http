package org.apache.coyote.http11.header;

public enum HeaderType {
    CONTENT_TYPE("Content-Type"),
    CACHE_CONTROL("Cache-Control"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    SERVER("Server"),
    EXPIRES("Expires"),
    CONTENT_LENGTH("Content-Length"),
    LAST_MODIFIED("Last-Modified"),
    ETAG("ETag"),
    KEEP_ALIVE("Keep-Alive"),
    DATE("Date"),
    HOST("Host"),
    CONNECTION("Connection"),
    ACCEPT_RANGES("Accept-Ranges");

    private final String header;

    HeaderType(final String header) {
        this.header = header;
    }

    public static HeaderType from(final String header) {
        for (HeaderType headerType : values()) {
            if (headerType.header.equalsIgnoreCase(header)) {
                return headerType;
            }
        }
        System.out.println("header = " + header);
        throw new IllegalArgumentException("지원하지 않는 헤더입니다.");
    }

    public String getHeader() {
        return header;
    }

}
