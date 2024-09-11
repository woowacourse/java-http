package org.apache.coyote.adapter;

import com.techcourse.controller.ResourceController;
import org.apache.coyote.http11.cookie.Cookies;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestMapper;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.SessionManager;

import java.time.LocalDateTime;
import java.util.Optional;

public class CoyoteAdapter implements Adapter{
    private final RequestMapper mapper;
    private final ResourceController resourceController;
    private final SessionManager sessionManager;

    public CoyoteAdapter(final RequestMapper mapper, final ResourceController resourceController, SessionManager sessionManager) {
        this.mapper = mapper;
        this.resourceController = resourceController;
        this.sessionManager = sessionManager;
    }

    @Override
    public void service(final HttpRequest req, final HttpResponse res) {
        sessionManager.cleanUpSession(LocalDateTime.now());
        setSession(req);

        Optional.ofNullable(mapper.mappingWithController(req))
                .ifPresentOrElse(
                        controller -> controller.service(req, res),
                        () -> resourceController.service(req, res));
    }

    private void setSession(final HttpRequest httpRequest) {

        sessionManager.findSession(httpRequest.getCookie(Cookies.SESSION_ID))
                .ifPresentOrElse(httpRequest::setSession,
                        () -> httpRequest.setSession(sessionManager.createSession(LocalDateTime.now())));
    }
}
