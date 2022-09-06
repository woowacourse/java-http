package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequestStartLine;

public class DefaultHandler extends Handler {

    public static DefaultHandler DEFAULT_HANDLER = new DefaultHandler();

    @Override
    public void handle(final HttpRequestStartLine startLine) {
    }
}
