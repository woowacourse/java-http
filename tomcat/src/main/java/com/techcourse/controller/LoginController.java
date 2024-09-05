package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.UUID;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private SessionManager sessionManager;
    HttpCookie httpCookie;

    public LoginController() {
        this.sessionManager = SessionManager.getInstance();
        this.httpCookie = new HttpCookie();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (request.hasCookie() && request.hasJSessionId()) {
            response.addHttpResponseHeader("Location", "/index.html");
            response.setHttpStatusCode(302);
            response.setHttpStatusMessage("FOUND");
            return;
        }
        response.setContentType(request);
        response.setHttpResponseBody(request.getUrlPath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        try {
            User user = InMemoryUserRepository.findByAccount(request.findRequestBodyBy("account"))
                    .orElseThrow(IllegalArgumentException::new);

            if (!user.checkPassword(request.findRequestBodyBy("password"))) {
                throw new IllegalArgumentException();
            }

            if (!request.hasCookie() || !request.hasJSessionId()) {
                UUID sessionId = UUID.randomUUID();
                Session session = new Session(sessionId.toString());
                session.setAttribute("user", user);
                sessionManager.add(session);

                UUID jSessionId = UUID.randomUUID();
                httpCookie.addJSessionId(jSessionId.toString());
                response.addHttpResponseHeader("Set-Cookie", httpCookie.getJSessionId());
            }
            response.addHttpResponseHeader("Location", "/index.html");
            response.setHttpStatusCode(302);
            response.setHttpStatusMessage("FOUND");
            response.setContentType(request);
            response.setHttpResponseBody(request.getUrlPath());
            log.info("user : {}", user);

        } catch (IllegalArgumentException e) {
            response.addHttpResponseHeader("Location", "/401.html");
            response.setHttpStatusCode(302);
            response.setHttpStatusMessage("FOUND");
            response.setContentType(request);
            response.setHttpResponseBody(request.getUrlPath());
        }
    }
}
