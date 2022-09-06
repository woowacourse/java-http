package org.apache.coyote.http11.http11handler.impl;

import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.http11response.ResponseComponent;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11request.Http11Request;

public class DefaultPageHandler implements Http11Handler {

    private static final String TARGET_URI = "/";
    private static final String DEFAULT_MESSAGE = "Hello world!";

    private HandlerSupporter handlerSupporter = new HandlerSupporter();

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        return http11Request.getUri().equals(TARGET_URI);
    }

    @Override
    public ResponseComponent handle(Http11Request http11Request) {
        return handlerSupporter.defaultResponseComponent(DEFAULT_MESSAGE, StatusCode.OK);
    }
}

