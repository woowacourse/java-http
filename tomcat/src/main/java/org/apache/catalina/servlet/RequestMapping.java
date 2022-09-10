package org.apache.catalina.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.apache.coyote.http11.http.RequestURI;

public class RequestMapping {
    private static final RequestMapping requestMapping = new RequestMapping();
    private static final Map<String, Servlet> urlMap = new HashMap<>();

    private RequestMapping() {
    }

    public static RequestMapping getRequestMapping() {
        return requestMapping;
    }

    public void addServlet(final String url, Servlet servlet) {
        urlMap.put(url, servlet);
    }

    public Optional<Servlet> getServlet(final RequestURI requestURI) {
        final String rowRequestURI = requestURI.getRequestURI();
        return urlMap.entrySet()
                .stream()
                .filter(it -> rowRequestURI.contains(it.getKey()))
                .map(Entry::getValue)
                .findFirst();
    }
}
