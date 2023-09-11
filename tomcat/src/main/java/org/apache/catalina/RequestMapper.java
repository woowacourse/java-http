package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.Mapper;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestMapper implements Mapper {

    private final Controller defaultController;
    private final Map<String, Controller> controllers = new HashMap<>();
    private final SessionManager sessionManager = new SessionManager();

    public RequestMapper(final Controller defaultController) {
        this.defaultController = defaultController;
    }

    @Override
    public void addController(final String path, final Controller controller) {
        controllers.put(path, controller);
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final RequestLine requestLine = httpRequest.getRequestLine();
        setSessionToHttpRequest(httpRequest);
        final Controller controller = controllers.getOrDefault(requestLine.parseUri(), defaultController);
        controller.service(httpRequest, httpResponse);
        addSession(httpResponse);
    }

    private void setSessionToHttpRequest(final HttpRequest httpRequest) {
        final String sessionId = httpRequest.parseSessionId();
        final Session session = sessionManager.findSession(sessionId);
        httpRequest.setSession(session);
    }

    private void addSession(final HttpResponse httpResponse) {
        final Session session = httpResponse.getSession();
        sessionManager.add(session);
    }
}
