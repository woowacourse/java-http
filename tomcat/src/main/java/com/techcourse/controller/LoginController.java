package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.MyHttpCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String USER = "user";
    private static final String PATH_LOGIN_HTML = "/login.html";
    private static final String PATH_INDEX_HTML = "/index.html";
    private static final String PATH_401_HTML = "401.html";


    private final SessionManager sessionManager;

    public LoginController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Session session = findSession(request);
        try {
            Map<String, String> parameters;
            if (request.isFormUrlEncoded()) {
                parameters = request.getFormData();
            } else {
                throw new IllegalArgumentException("지원하지 않는 Content-Type입니다: " + request.getContentType());
            }
            Optional<User> optionalUser = InMemoryUserRepository.findByAccount(parameters.get("account"));
            if (optionalUser.isEmpty()) {
                throw new IllegalArgumentException("아이디와 비밀번호를 다시 확인하고 입력해주세요.");
            }
            User user = optionalUser.get();
            if (!user.checkPassword(parameters.get("password"))) {
                throw new IllegalArgumentException("아이디와 비밀번호를 다시 확인하고 입력해주세요.");
            }
            log.info(user.toString());
            if (session == null) {
                Session newSession = new Session(UUID.randomUUID().toString());
                newSession.setAttribute(USER, user);
                sessionManager.add(newSession);
                response.sendRedirect(PATH_INDEX_HTML);
                response.setCookie(newSession.getId());
                return;
            }
            session.setAttribute(USER, user);
            response.sendRedirect(PATH_INDEX_HTML);
        } catch (IllegalArgumentException exception) {
            response.sendRedirect(PATH_401_HTML);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException, URISyntaxException {
        Session session = findSession(request);
        if (session != null && session.getAttribute(USER) != null) {
            response.sendRedirect(PATH_INDEX_HTML);
            return;
        }
        response.sendStaticHtml(PATH_LOGIN_HTML);
    }

    private Session findSession(final HttpRequest request) {
        MyHttpCookie cookie = new MyHttpCookie(request.getCookie());
        return sessionManager.findSession(cookie.getJSessionId());
    }
}
