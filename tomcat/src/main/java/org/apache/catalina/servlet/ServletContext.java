package org.apache.catalina.servlet;

import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;

public class ServletContext {

    private final List<HttpServlet> servlets = new ArrayList<>();
    private final SessionManager sessionManager = new SessionManager(new UUIDSessionIdGenerator());

    public void addServlet(HttpServlet servlet) {
        servlets.add(servlet);
    }

    public HttpServlet mapServlet(HttpRequest request) {
        request.setManager(sessionManager);
        return servlets.stream()
                .filter(servlet -> servlet.canService(request))
                .findFirst()
                .orElse(StaticResourceServlet.getInstance());
    }
}
