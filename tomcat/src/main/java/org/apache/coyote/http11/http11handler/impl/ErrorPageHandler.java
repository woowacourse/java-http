package org.apache.coyote.http11.http11handler.impl;

import java.util.List;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.dto.ResponseComponent;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11request.Http11Request;

public class ErrorPageHandler implements Http11Handler {

    private static final List<String> SUPPORT_URI = List.of("/401.html", "/404.html", "/500.html");

    private HandlerSupporter handlerSupporter = new HandlerSupporter();

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        return SUPPORT_URI.contains(http11Request.getUri());
    }

    @Override
    public ResponseComponent handle(Http11Request http11Request) {
        return handlerSupporter.resourceResponseComponent(http11Request.getUri(), StatusCode.OK);
    }
}
