package com.techcourse.controller;

import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.io.IOException;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.handler.HttpHandler;
import org.apache.coyote.http11.message.HttpCookie;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPostController implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginGetController.class);
    private static final String INDEX_HTML_URL = "http://localhost:8080/index.html";
    private static final String UNAUTHORIZED_HTML_URL = "http://localhost:8080/401.html";

    private final UserService service;

    public LoginPostController(UserService service) {
        this.service = service;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        validateFormParameters(request);

        try {
            User user = findUser(request);
            log.info("로그인 성공! account: {}", user.getAccount());

            HttpResponse response = HttpResponse.found(INDEX_HTML_URL);

            if (!request.hasSession()) {
                Session session = addSession(user);
                response.setCookie(HttpCookie.from(session));
            }

            return response;
        } catch (UnauthorizedException e) {
            return HttpResponse.found(UNAUTHORIZED_HTML_URL);
        }
    }

    private void validateFormParameters(HttpRequest request) {
        if (!request.hasFormParameters()) {
            throw new IllegalArgumentException("로그인에 필요한 데이터가 오지 않았습니다.");
        }
    }

    private User findUser(HttpRequest request) {
        String account = request.getFormParameter("account");
        String password = request.getFormParameter("password");

        return service.findUserByAccountAndPassword(account, password);
    }

    private Session addSession(User user) {
        Session session = Session.create();
        session.setAttribute("user", user);
        SessionManager.getInstance()
                .add(session);

        return session;
    }
}
