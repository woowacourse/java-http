package org.apache.tomcat.util.http.header;

public record HttpHeaderType(String header) {

    public static final HttpHeaderType CONTENT_TYPE = new HttpHeaderType("Content-Type");
    public static final HttpHeaderType CONTENT_LENGTH = new HttpHeaderType("Content-Length");
    public static final HttpHeaderType COOKIE = new HttpHeaderType("Cookie");
    public static final HttpHeaderType SET_COOKIE = new HttpHeaderType("Set-Cookie");
    public static final HttpHeaderType LOCATION = new HttpHeaderType("Location");
}
