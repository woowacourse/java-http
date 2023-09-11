package org.apache.coyote.http11.common.header;

public class SetCookie extends Header {

    private final HttpCookie httpCookie;

    public SetCookie(final HttpCookie httpCookie) {
        super(HeaderName.SET_COOKIE);
        this.httpCookie = httpCookie;
    }

    @Override
    public String convertToString() {
        return new StringBuilder().append(getHeaderPropertyName()).append(HEADER_KEY_VALUE_DELIMITER)
                                  .append(httpCookie.convertToString()).append(SPACE)
                                  .toString();
    }
}
