package org.apache.coyote.http11.http11handler.impl;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.http11response.ResponseComponent;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11request.Http11Request;

public class LoginPageHandler implements Http11Handler {

    private static final String URI = "/login";
    private static final String URI_WITH_EXTENSION = "/login.html";
    private static final HttpMethod HTTP_METHOD = HttpMethod.GET;

    private HandlerSupporter handlerSupporter = new HandlerSupporter();

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        return URI.equals(http11Request.getUri()) && http11Request.getHttpMethod().equals(HTTP_METHOD);
    }

    @Override
    public ResponseComponent handle(Http11Request http11Request) {
        return handlerSupporter.resourceResponseComponent(URI_WITH_EXTENSION, StatusCode.OK);
    }

}
