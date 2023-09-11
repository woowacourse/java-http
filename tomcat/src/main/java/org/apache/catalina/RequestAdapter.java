package org.apache.catalina;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.Adapter;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestAdapter implements Adapter {

    private final RequestMapper requestMapper;
    private final SessionManager sessionManager = new SessionManager();

    public RequestAdapter(final RequestMapper requestMapper) {
        this.requestMapper = requestMapper;
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final RequestLine requestLine = httpRequest.getRequestLine();
        final Controller controller = requestMapper.getController(requestLine.parseUri());
        setSessionToHttpRequest(httpRequest);
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
