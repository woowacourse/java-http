package com.techcourse.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontController {

    private static final Logger log = LoggerFactory.getLogger(FrontController.class);

    private final Map<String, RequestHandler> handlers = new HashMap<>();

    public FrontController() {
        handlers.put("/", new IndexHandler());
        handlers.put("/login", new LoginHandler());
        handlers.put("/register", new RegisterHandler());
        handlers.put("/user", new UserHandler());
    }

    public void handleRequest(HttpRequest request, HttpResponse response) throws IOException {
        RequestHandler handler = handlers.get(request.getPath());
        log.debug("Request Line: {}", request.getRequestLine());
        log.debug("Body: {}", request.getParams());

        if (handler != null) {
            handler.handle(request, response);
            return;
        }

        List<String> allowedStaticResources = List.of("html", "css", "js", "png", "jpg", "ico", "svg");
        if (allowedStaticResources.contains(request.getExtension())) {
            new StaticResourceHandler().handle(request, response);
            return;
        }

        response.sendRedirect("/404.html");
        response.write();
    }
}
