package org.apache.coyote.http;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class HttpConstants {

    // Protocol
    public static final String HTTP_PROTOCOL_PREFIX = "HTTP/";
    public static final String CONTENT_LENGTH_HEADER_NAME = "content-length";
    public static final String CRLF = "\r\n";
    public static final String QUERY_STRING = "?";

    // Separators
    public static final String KEY_VALUE_SEPARATOR = "=";
    public static final String PARAM_SEPARATOR = "&";
    public static final String HEADER_VALUE_SEPARATOR = ":";
}
