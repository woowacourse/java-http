package org.apache.catalina.servlet;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;

public class ServletContext {

    private final Map<String, HttpServlet> requestMappings = new LinkedHashMap<>();
    private final SessionManager sessionManager = new SessionManager(new UUIDSessionIdGenerator());

    public void addServlet(String path, HttpServlet servlet) {
        requestMappings.put(path, servlet);
    }

    public HttpServlet mapServlet(HttpRequest request) {
        request.setManager(sessionManager);
        String path = request.getPath();
        return requestMappings.getOrDefault(path, StaticResourceServlet.getInstance());
    }
}
