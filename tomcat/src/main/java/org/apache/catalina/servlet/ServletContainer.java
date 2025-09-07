package org.apache.catalina.servlet;

import java.util.HashMap;
import java.util.Map;

public class ServletContainer {
    private final Map<String, Servlet> servlets = new HashMap<>();

    public void add(String path, Servlet servlet) {
        servlets.put(path, servlet);
    }

    public Servlet getServletBy(String path) {
        return servlets.get(path);
    }
}
