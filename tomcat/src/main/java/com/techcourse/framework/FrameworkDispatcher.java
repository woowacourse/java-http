package com.techcourse.framework;

import com.techcourse.framework.handler.Controller;
import org.apache.coyote.Dispatcher;
import org.apache.coyote.http11.protocol.request.HttpRequest;
import org.apache.coyote.http11.protocol.response.HttpResponse;

public class FrameworkDispatcher implements Dispatcher {

    private final RequestMapping requestMapping;

    public FrameworkDispatcher(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    @Override
    public void dispatch(HttpRequest request, HttpResponse response) {
        Controller controller = requestMapping.getController(request);
        controller.service(request, response);
    }
}
