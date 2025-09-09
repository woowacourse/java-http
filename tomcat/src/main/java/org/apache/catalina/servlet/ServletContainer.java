package org.apache.catalina.servlet;

import java.util.HashMap;
import java.util.Map;

//TODO: fallBackServlet을 application 개발자가 지정하는게 맞을지 검토  (2025-09-9, 화, 21:6)
// https://github.com/woowacourse/java-http/pull/899#discussion_r2331128262
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

    public Servlet getServletBy(String path) {
        return servlets.getOrDefault(path, fallBackServlet);
    }
}
