package org.apache.coyote.http11.dispatcher.handlerAdapter;

import com.techcourse.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ControllerHandlerAdapter implements HandlerAdapter1 {

    @Override
    public boolean supports(Object handler) {
        return handler instanceof Controller;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest, Object handler) throws Exception {
        HttpResponse httpResponse = new HttpResponse();
        ((Controller) handler).service(httpRequest, httpResponse);
        return httpResponse;
    }
}
