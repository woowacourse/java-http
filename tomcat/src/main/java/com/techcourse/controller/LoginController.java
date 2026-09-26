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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        MyHttpCookie httpCookie = new MyHttpCookie(request.getCookie());
        Session session = sessionManager.findSession(httpCookie.getJSessionId());
        try {
            Map<String, String> parameters;
            if (request.isFormUrlEncoded()) {
                parameters = parseFormData(request.getBody());
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
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        MyHttpCookie httpCookie = new MyHttpCookie(request.getCookie());
        Session session = sessionManager.findSession(httpCookie.getJSessionId());
        if (session != null && session.getAttribute(USER) != null) {
            response.sendRedirect(PATH_INDEX_HTML);
            return;
        }
        response.sendStaticHtml(PATH_LOGIN_HTML);
    }

    private Map<String, String> parseFormData(final String body) {
        Map<String, String> formData = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyAndMap = pair.split("=", 2);
            String key = keyAndMap[0];
            String value = keyAndMap[1];
            formData.put(URLDecoder.decode(key, StandardCharsets.UTF_8), URLDecoder.decode(value, StandardCharsets.UTF_8));
        }
        return formData;
    }
}
