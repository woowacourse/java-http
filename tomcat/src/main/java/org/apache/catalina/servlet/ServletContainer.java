package org.apache.catalina.servlet;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.ContentType;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class ServletContainer {
    private static final ServletContainer INSTANCE = new ServletContainer();

    private final Map<String, Servlet> servlets = new HashMap<>();
    private Servlet fallBackServlet;

    private ServletContainer() {
    }

    public static ServletContainer getInstance() {
        return INSTANCE;
    }

    public void add(String path, Servlet servlet) {
        servlets.put(path, servlet);
    }

    public void setFallBackServlet(Servlet fallBackServlet) {
        this.fallBackServlet = fallBackServlet;
    }

    public void executeServlet(HttpRequest request, HttpResponse response) {
        Servlet servlet = servlets.getOrDefault(request.getRequestPath(), fallBackServlet);
        if (servlet == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setContentType(ContentType.PLAIN);
            response.appendToBody("404 Not Found".getBytes());
            return;
        }

        servlet.service(request, response);
    }
}
