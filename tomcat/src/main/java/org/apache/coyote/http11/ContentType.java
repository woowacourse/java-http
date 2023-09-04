package org.apache.coyote.http11;

public class ContentType extends HttpHeader {

    private static final String DEFAULT_CHARSET = ";charset=utf-8";

    protected ContentType(final String value) {
        super("Content-Type", value + DEFAULT_CHARSET);
    }
}
