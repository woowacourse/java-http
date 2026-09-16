package com.techcourse.web;

import com.techcourse.controller.ApplicationController;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.Dispatcher;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class ApplicationDispatcher implements Dispatcher {

    private final ApplicationController applicationController;
    private final StaticResourceHandler staticResourceHandler;

    public ApplicationDispatcher(ApplicationController applicationController, StaticResourceHandler staticResourceHandler) {
        this.applicationController = applicationController;
        this.staticResourceHandler = staticResourceHandler;
    }

    @Override
    public HttpResponse dispatch(HttpRequest httpRequest) throws IOException {
        String path = httpRequest.path();
        Map<String, String> parameters = httpRequest.parameters();

        if ("/login".equals(path)) {
            String responsePath = applicationController.login(parameters);
            return staticResourceHandler.createResponse(responsePath);
        }

        if ("/".equals(path)) {
            return HttpResponse.ok(
                    "text/html;charset=utf-8",
                    "Hello world!".getBytes(StandardCharsets.UTF_8)
            );
        }

        return staticResourceHandler.createResponse(path);
    }
}
