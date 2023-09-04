package org.apache.coyote.http11.header;

public class ContentType extends HttpHeader {

    private static final String DEFAULT_CHARSET = ";charset=utf-8";

    public ContentType(final String value) {
        super("Content-Type", value + DEFAULT_CHARSET);
    }
}
