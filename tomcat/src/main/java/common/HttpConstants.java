package common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class HttpConstants {

    // Protocol
    public static final String HTTP_PROTOCOL_PREFIX = "HTTP/";
    public static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";
    public static final String LOCATION_HEADER_NAME = "Location";
    public static final String COOKIE_HEADER_NAME = "Cookie";
    public static final String SET_COOKIE_HEADER_NAME = "Set-Cookie";
    public static final String CRLF = "\r\n";
    public static final String QUERY_STRING = "?";

    // Separators
    public static final String KEY_VALUE_SEPARATOR = "=";
    public static final String VALUE_SEPARATOR = ";";
    public static final String PARAM_SEPARATOR = "&";
    public static final String HEADER_VALUE_SEPARATOR = ":";
}
