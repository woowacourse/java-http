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

    private final HttpHeaderName name;
    private final String value;

    private HttpHeader(final HttpHeaderName name, final String value) {
        this.name = name;
        this.value = value;
    }

    public static HttpHeader contentType(final FileType fileType) {
        return new HttpHeader(CONTENT_TYPE, fileType.getValue() + ENCODING);
    }

    public static HttpHeader contentLengthOf(final String responseBody) {
        return new HttpHeader(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
    }

    public static HttpHeader location(final String location) {
        return new HttpHeader(LOCATION, location);
    }

    public static HttpHeader setCookie(final HttpCookie cookie) {
        return new HttpHeader(SET_COOKIE, cookie.parseCookiesToQueryString());
    }

    public static HttpHeader cookie(final HttpCookie cookie) {
        return new HttpHeader(COOKIE, cookie.parseCookiesToQueryString());
    }

    public String getHeaderAsString() {
        return name.getName() + DELIMITER + value + BLANK;
    }
}
