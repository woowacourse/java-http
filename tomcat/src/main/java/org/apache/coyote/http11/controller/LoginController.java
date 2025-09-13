package org.apache.coyote.http11.controller;

import com.techcourse.model.User;
import java.io.IOException;
import java.util.UUID;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.request.UserRegisterManager;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController{

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final SessionManager sessionManager;

    public LoginController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response, RequestLine requestLine) throws Exception {
        response.sendResponse(response.getResponse(requestLine.getPath()));
        response.sendFile(requestLine.getPath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        UserRegisterManager userRegisterManager = UserRegisterManager.of(request.getRequestBody());

        if (userRegisterManager.isExistsUser()) {
            User user = userRegisterManager.getUser();
            log.info("User: account = {}, password = {}", user.getAccount(), user.getPassword());

            if (userRegisterManager.isPasswordCorrect()) {
                String cookieSession = getSession(user);
                response.sendResponse(response.buildRedirectHeaders("/index.html", cookieSession));
                return;
            }
        }

        response.sendResponse(response.buildRedirectHeaders("/401.html"));
    }

    private String getSession(User user) {
        String cookieSession = UUID.randomUUID().toString();
        Session session = new Session(cookieSession);

        session.setAttribute("user", user);
        sessionManager.add(session);

        return cookieSession;
    }
}
