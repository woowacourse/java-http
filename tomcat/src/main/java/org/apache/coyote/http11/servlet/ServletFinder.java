package org.apache.coyote.http11.servlet;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.request.HttpRequest;

public class ServletFinder {
    private static final Servlet DEFAULT_SERVLET;
    private static final Map<String, Servlet> servlets;

    static {
        servlets = new HashMap<>();
        servlets.put("/", new HelloWorldServlet());
        servlets.put("/login", new LoginServlet());
        servlets.put("/register", new RegisterServlet());

        DEFAULT_SERVLET = new DefaultServlet();
    }

    public static Servlet find(HttpRequest request) {
        String path = request.getUri().getDetail();
        return servlets.getOrDefault(path, DEFAULT_SERVLET);
    }
}
