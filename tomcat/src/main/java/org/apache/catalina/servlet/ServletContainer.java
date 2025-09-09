package org.apache.catalina.servlet;

import java.util.HashMap;
import java.util.Map;

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
