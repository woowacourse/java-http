package org.apache.catalina.servlet.adapter;

import nextstep.jwp.controller.UserHandler;
import nextstep.jwp.controller.StaticHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.start.HttpMethod;

import static org.apache.coyote.http11.request.start.HttpMethod.*;

public class HandlerAdapter {

    public static Handler getHandler(HttpRequest request) {
        String requestURI = request.getPath();
        final HttpMethod httpMethod = request.getHttpMethod();
        if (httpMethod.equals(POST) && (requestURI.equals("/login") || requestURI.equals("/register"))) {
            return new UserHandler();
        }
        if (httpMethod.equals(GET)) {
            return new StaticHandler();
        }
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }
}
