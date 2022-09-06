package org.apache.coyote.http11.http11handler.impl;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.dto.ResponseComponent;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11request.Http11Request;

public class RegisterPageHandler implements Http11Handler {

    private static final String URI = "/register";
    private static final String URI_WITH_EXTENSION = "/register.html";
    private static final HttpMethod ALLOWED_HTTP_METHOD = HttpMethod.GET;

    private HandlerSupporter handlerSupporter = new HandlerSupporter();

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        return http11Request.getUri().equals(URI) && http11Request.getHttpMethod().equals(ALLOWED_HTTP_METHOD);
    }

    @Override
    public ResponseComponent handle(Http11Request http11Request) {
        return new ResponseComponent(
                StatusCode.OK,
                handlerSupporter.getContentType(URI_WITH_EXTENSION),
                handlerSupporter.getContentLength(URI_WITH_EXTENSION),
                handlerSupporter.extractBody(URI_WITH_EXTENSION)
        );
    }
}
