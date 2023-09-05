package org.apache.coyote.http11.handler.adapter;

import nextstep.jwp.controller.base.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HandlerAdapter {

    public static HttpResponse adaptController(final Controller controller, final HttpRequest httpRequest) throws Exception {
        return controller.handle(httpRequest);
    }
}
