package org.apache.coyote.http11;

import java.util.Map;
import java.util.Set;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Set<String> STATIC_RESOURCES = Set.of(
            "/401.html",
            "/404.html",
            "/500.html",
            "/login.html",
            "/register.html"
    );

    private final RequestMapping requestMapping = RequestMapping.from(
            Map.of(
                    RequestUri.from("/login"), new LoginController(),
                    RequestUri.from("/register"), new RegisterController()
            )
    );

    public static Http11RequestHandler from() {
        return new Http11RequestHandler();
    }

    public void handle(Http11Request request, Http11Response response) {
        if (isStaticSource(request, response)) {
            return;
        }
        Controller controller = requestMapping.getController(request);
        try {
            controller.service(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.internalServerError(request);
        }
    }

    private boolean isStaticSource(Http11Request request, Http11Response response) {
        RequestLine requestLine = request.getRequestLine();
        HttpMethod httpMethod = requestLine.getHttpMethod();
        RequestUri requestUri = requestLine.getRequestUri();
        String path = requestUri.getPath();

        if (httpMethod.isGet() && STATIC_RESOURCES.contains(path)) {
            response.notFound(request);
            return true;
        }
        return false;
    }
}
