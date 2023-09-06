package org.apache.coyote.http11.servlet;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.request.RequestUri;

public class ServletFinder {
    private static final Map<String, Servlet> servlets;
    private static final Servlet STATIC_RESOURCE_SERVLET;
    private static final Servlet NOT_FOUND_SERVLET;

    static {
        servlets = new HashMap<>();
        servlets.put("/", new HelloWorldServlet());
        servlets.put("/login", new LoginServlet());
        servlets.put("/register", new RegisterServlet());

        STATIC_RESOURCE_SERVLET = new StaticResourceServlet();
        NOT_FOUND_SERVLET = new NotFoundServlet();
    }

    public static Servlet find(HttpRequest request) {
        RequestUri uri = request.getUri();
        if (!uri.getExtension().isBlank()) {
            return STATIC_RESOURCE_SERVLET;
        }
        String path = uri.getValue();
        return servlets.getOrDefault(path, NOT_FOUND_SERVLET);
    }
}
