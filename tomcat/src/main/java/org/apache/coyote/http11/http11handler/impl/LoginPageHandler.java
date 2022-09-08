package org.apache.coyote.http11.http11handler.impl;

import nextstep.jwp.model.visitor.Visitor;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11response.Http11Response;

public class LoginPageHandler implements Http11Handler {

    private static final String URI = "/login";
    private static final String URI_WITH_EXTENSION = "/login.html";
    private static final String REDIRECT_URI_ALREADY_LOGIN = "/index.html";
    private static final HttpMethod HTTP_METHOD = HttpMethod.GET;

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        return URI.equals(http11Request.getUri()) && http11Request.getHttpMethod().equals(HTTP_METHOD);
    }

    @Override
    public Http11Response handle(Http11Request http11Request, Visitor visitor) {
        if (visitor.isLogin()) {
            return HandlerSupporter.redirectResponseComponent(REDIRECT_URI_ALREADY_LOGIN, StatusCode.REDIRECT);
        }
        return HandlerSupporter.resourceResponseComponent(URI_WITH_EXTENSION, StatusCode.OK);
    }

}
