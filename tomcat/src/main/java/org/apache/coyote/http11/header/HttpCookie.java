package org.apache.coyote.http11.header;

import java.util.List;

public class HttpCookie extends HttpHeader {

    private static final String SET_COOKIE = "Set-Cookie";

    private HttpCookie(final String httpHeaderType, final List<String> values) {
        super(httpHeaderType, values);
    }

    public static HttpCookie of(final HttpHeader httpHeader) {
        return new HttpCookie(httpHeader.getHttpHeaderType(), httpHeader.getValues());
    }

    public static HttpHeader generateResponseHeader(final String value) {
        return HttpHeader.of(SET_COOKIE, value);
    }
}
