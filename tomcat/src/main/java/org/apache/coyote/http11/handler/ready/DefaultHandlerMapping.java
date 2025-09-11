package org.apache.coyote.http11.handler.ready;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.dto.HttpRequest;

public class DefaultHandlerMapping implements HandlerMapping{
    @Override
    public int getOrder() {
        return 999;
    }

    @Override
    public Controller getHandler(HttpRequest request) {
        return null; //new NotFoundController();  // 404 처리
    }
}
