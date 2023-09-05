package org.apache.coyote.http11.handler.adapter;

import org.apache.coyote.http11.handler.controller.base.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HandlerAdapter {

    public static HttpResponse handlerController(final Controller controller, final HttpRequest httpRequest) throws Exception {
        return controller.service(httpRequest);
    }
}
