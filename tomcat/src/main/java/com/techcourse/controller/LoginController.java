package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final SessionManager sessionManager;

    public LoginController() {
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        Session session = sessionManager.findSession(request.getJSessionId());
        if (isAuthenticated(session)) {
            response.addHttpResponseHeader(HttpHeader.LOCATION, "/index.html");
            response.setHttpStatus(HttpStatus.FOUND);
            return;
        }
        response.setContentType(ContentType.TEXT_HTML);
        response.setHttpResponseBody(request.getUrlPath());
    }

    private boolean isAuthenticated(Session session) {
        return session != null && session.getAttribute("user") != null;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        try {
            User user = InMemoryUserRepository.findByAccount(request.findRequestBodyBy("account"))
                    .orElseThrow(IllegalArgumentException::new);

            if (!user.checkPassword(request.findRequestBodyBy("password"))) {
                throw new IllegalArgumentException();
            }

            UUID sessionId = UUID.randomUUID();
            Session session = new Session(sessionId.toString());
            session.setAttribute("user", user);
            sessionManager.add(session);

            response.setJSessionId(session.getId());
            response.addHttpResponseHeader(HttpHeader.LOCATION, "/index.html");
            response.setHttpStatus(HttpStatus.FOUND);
            response.setContentType(ContentType.TEXT_HTML);
            response.setHttpResponseBody(request.getUrlPath());
            log.info("user : {}", user);

        } catch (IllegalArgumentException e) {
            response.addHttpResponseHeader(HttpHeader.LOCATION, "/401.html");
            response.setHttpStatus(HttpStatus.FOUND);
            response.setContentType(ContentType.TEXT_HTML);
            response.setHttpResponseBody(request.getUrlPath());
        }
    }
}
