package com.techcourse.servlet;

import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.servlet.Servlet;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DispatcherServlet implements Servlet {

    private final RequestMapping requestMapping;

    public DispatcherServlet(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    @Override
    public boolean canService(HttpRequest request) {
        return requestMapping.getController(request).isPresent();
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        Optional<Controller> controller = requestMapping.getController(request);

        if (controller.isPresent()) {
            controller.get().service(request, response);
        }
    }
}
