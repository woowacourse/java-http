package org.apache.catalina.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.http.HttpRequest;

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

    public Servlet getServlet(final HttpRequest httpRequest) {
        return urlMap.entrySet()
                .stream()
                .filter(it -> httpRequest.containUrl(it.getKey()))
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(NotFoundServletException::new);
    }
}
