package org.apache.catalina;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.IndexController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.WelcomeController;
import org.apache.catalina.util.ResourceFile;
import org.apache.catalina.util.ResourceReader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletContainer {

    private static final List<String> STATIC_RESOURCE_EXTENSION = List.of(".html", ".css", ".js");
    private static final Logger log = LoggerFactory.getLogger(ServletContainer.class);

    private final Map<String, Controller> servletMapping;

    public ServletContainer() {
        Map<String, Controller> servletsMapping = new HashMap<>();

        servletsMapping.put("/", new WelcomeController());
        servletsMapping.put("/login", new LoginController());
        servletsMapping.put("/register", new RegisterController());
        servletsMapping.put("/index", new IndexController());

        this.servletMapping = servletsMapping;
    }

    public HttpResponse dispatch(HttpRequest request) throws Exception {
        String requestUrl = request.getRequestUrl();
        HttpResponse response = new HttpResponse();

        if (requestUrl.equals("/")) {
            servletMapping.get("/")
                    .service(request, response);
            return response;
        }

        if (isStaticResource(requestUrl)) {
            ResourceFile resourceFile = ResourceReader.readResource(requestUrl);
            response.setBody(resourceFile);
            response.setStatus(HttpStatus.OK);
            return response;
        }

        servletMapping.keySet().stream()
                .filter(requestUrl::startsWith)
                .findFirst()
                .ifPresentOrElse(
                        mappingUrl -> doService(mappingUrl, request, response),
                        () -> response.setRedirect("/404.html")
                );

        return response;
    }

    private boolean isStaticResource(String requestUrl) {
        return STATIC_RESOURCE_EXTENSION.stream()
                .anyMatch(requestUrl::endsWith);
    }

    private void doService(String mappingUrl, HttpRequest request, HttpResponse response) {
        try {
            servletMapping.get(mappingUrl)
                    .service(request, response);
        } catch (Exception e) {
            log.warn("예외 메시지 = {}", e.getMessage(), e);
            throw new IllegalStateException("요청 처리 중 예외가 발생했습니다.");
        }
    }
}
