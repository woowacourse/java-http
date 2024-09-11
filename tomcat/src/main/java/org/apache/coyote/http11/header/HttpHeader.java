package org.apache.coyote.http11.header;

import static org.apache.coyote.http11.header.HttpHeaderName.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.HttpHeaderName.CONTENT_TYPE;
import static org.apache.coyote.http11.header.HttpHeaderName.COOKIE;
import static org.apache.coyote.http11.header.HttpHeaderName.LOCATION;
import static org.apache.coyote.http11.header.HttpHeaderName.SET_COOKIE;

import org.apache.coyote.http11.FileType;
import org.apache.coyote.http11.HttpCookie;

public class HttpHeader {
    private static final String DELIMITER = ": ";
    private static final String BLANK = " ";
    public static final String ENCODING = ";charset=utf-8";

    public static String contentType(final FileType fileType) {
        return CONTENT_TYPE.getName() + DELIMITER + fileType.getValue() + ENCODING + BLANK;
    }

    public static String contentLengthOf(final String responseBody) {
        return CONTENT_LENGTH.getName() + DELIMITER + responseBody.getBytes().length + BLANK;
    }

    public static String location(final String location) {
        return LOCATION.getName() + DELIMITER + location + BLANK;
    }

    public static String setCookie(final HttpCookie cookie) {
        return SET_COOKIE.getName() + DELIMITER + cookie.parseCookiesToQueryString() + BLANK;
    }

    public static String cookie(final HttpCookie cookie) {
        return COOKIE.getName() + DELIMITER + cookie.parseCookiesToQueryString() + BLANK;
    }

    private HttpHeader() {
    }
}
