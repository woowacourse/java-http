package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.DefaultServlet;
import org.apache.catalina.controller.IndexController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class ServletContainer {

    private final Map<String, Controller> servletMapping;
    private final Controller defaultServlet;

    public ServletContainer() {
        Map<String, Controller> servletsMapping = new HashMap<>();

        servletsMapping.put("/login", new LoginController());
        servletsMapping.put("/register", new RegisterController());
        servletsMapping.put("/index", new IndexController());

        this.servletMapping = servletsMapping;
        this.defaultServlet = new DefaultServlet();
    }

    public HttpResponse dispatch(HttpRequest request) throws Exception {
        String requestUrl = request.getRequestUrl();

        HttpHeader responseHeader = new HttpHeader();

        if (requestUrl.equals("/")) {
            String responseBody = "Hello world!";
            responseHeader.addHeader("Content-Type", "text/html;charset=utf-8");
            responseHeader.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
            return new HttpResponse(responseHeader, HttpStatus.OK, responseBody);
        }

        HttpResponse response = new HttpResponse();
        if (Stream.of(".html", ".css", ".js").anyMatch(requestUrl::endsWith)) {
            defaultServlet.service(request, response);
            return response;
        }

        servletMapping.keySet().stream()
                .filter(requestUrl::startsWith)
                .findFirst()
                .ifPresentOrElse(mappingUrl -> {
                    try {
                        servletMapping.get(mappingUrl).service(request, response);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, () -> response.setRedirect("/404.html"));

        return response;
    }
}
