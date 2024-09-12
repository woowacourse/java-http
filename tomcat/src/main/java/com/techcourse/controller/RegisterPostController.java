package com.techcourse.controller;

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

public class RegisterPostController implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(RegisterPostController.class);

    private final UserService service;

    public RegisterPostController(UserService service) {
        this.service = service;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        validateFormParameters(request);

        User user = saveUser(request);
        log.info("회원가입 성공! account: {}, email: {}", user.getAccount(), user.getEmail());

        HttpResponse response = HttpResponse.found("http://localhost:8080/index.html");
        if (!request.hasSession()) {
            Session session = addSession(user);
            response.setCookie(HttpCookie.from(session));
        }

        return response;
    }

    private void validateFormParameters(HttpRequest request) {
        if (!request.hasFormParameters()) {
            throw new IllegalArgumentException("로그인에 필요한 데이터가 오지 않았습니다.");
        }
    }

    private User saveUser(HttpRequest request) {
        String account = request.getFormParameter("account");
        String password = request.getFormParameter("password");
        String email = request.getFormParameter("email");

        return service.saveUser(account, password, email);
    }

    private Session addSession(User user) {
        Session session = Session.create();
        session.setAttribute("user", user);
        SessionManager.getInstance()
                .add(session);

        return session;
    }
}
