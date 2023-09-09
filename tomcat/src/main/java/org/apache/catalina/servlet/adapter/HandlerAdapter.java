package org.apache.catalina.servlet.adapter;

import nextstep.jwp.controller.UserController;
import org.apache.coyote.http11.request.HttpRequest;

public class HandlerAdapter {

    public static Controller getHandler(HttpRequest request) {
        String requestURI = request.getPath();
        if (requestURI.equals("/login")||requestURI.equals("/register")) {
            return new UserController();
        }
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }
}
