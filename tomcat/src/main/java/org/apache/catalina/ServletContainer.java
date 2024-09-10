package org.apache.catalina;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.IndexController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.util.ResourceReader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class ServletContainer {

    private static final List<String> STATIC_RESOURCE_EXTENSION = List.of(".html", ".css", ".js");

    private final Map<String, Controller> servletMapping;

    public ServletContainer() {
        Map<String, Controller> servletsMapping = new HashMap<>();

        servletsMapping.put("/login", new LoginController());
        servletsMapping.put("/register", new RegisterController());
        servletsMapping.put("/index", new IndexController());

        this.servletMapping = servletsMapping;
    }

    public HttpResponse dispatch(HttpRequest request) throws IOException {
        String requestUrl = request.getRequestUrl();
        HttpResponse response = new HttpResponse();

        if (requestUrl.equals("/")) {
            response.setBody("Hello world!", "text/html");
            response.setStatus(HttpStatus.OK);
            return response;
        }

        if (isStaticResource(requestUrl)) {
            ResourceReader.serveResource(requestUrl, response);
            return response;
        }

        servletMapping.keySet().stream()
                .filter(requestUrl::startsWith)
                .findFirst()
                .ifPresentOrElse(
                        doService(request, response),
                        () -> response.setRedirect("/404.html")
                );

        return response;
    }

    private boolean isStaticResource(String requestUrl) {
        return STATIC_RESOURCE_EXTENSION.stream()
                .anyMatch(requestUrl::endsWith);
    }

    private Consumer<String> doService(HttpRequest request, HttpResponse response) {
        return mappingUrl -> {
            try {
                servletMapping.get(mappingUrl).service(request, response);
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage());
            }
        };
    }
}
