package com.techcourse.handler;

import java.io.IOException;
import java.util.List;
import org.apache.catalina.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(FrontController.class);
    public static final List<String> ALLOWED_STATIC_RESOURCES = List.of("html", "css", "js", "png", "jpg", "ico", "svg");

    private final HandlerMapping handlerMapping;

    public FrontController(HandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public void service(HttpRequest request, HttpResponse response) throws IOException {
        Controller handler = handlerMapping.getController(request);
        log.debug("Request Line: {}", request.getRequestLine());
        log.debug("Body: {}", request.getParams());

        if (handler != null) {
            handler.service(request, response);
            return;
        }

        if (ALLOWED_STATIC_RESOURCES.contains(request.getExtension())) {
            new StaticResourceHandler().service(request, response);
            return;
        }

        new NotFoundHandler().service(request, response);
    }
}
