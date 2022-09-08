package org.apache.coyote.http11.http11handler.impl;

import nextstep.jwp.model.visitor.Visitor;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11response.Http11Response;

public class DefaultPageHandler implements Http11Handler {

    private static final String TARGET_URI = "/";
    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        return http11Request.getUri().equals(TARGET_URI);
    }

    @Override
    public Http11Response handle(Http11Request http11Request, Visitor visitor) {
        return HandlerSupporter.defaultResponseComponent(DEFAULT_MESSAGE, StatusCode.OK);
    }
}

