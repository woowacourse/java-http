package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.domain.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.cookie.HttpCookie;
import org.apache.coyote.http.cookie.HttpCookies;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LoginController extends AbstractController {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String USER_KEY = "user";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED = "/401.html";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            Map<String, String> loginUserInfo = parseUserInfo(request.getRequestBody());
            User loginUser = login(loginUserInfo);
            createNewSession(request, response, loginUser);
            redirect(INDEX_PAGE, response);

        } catch (IllegalArgumentException e) {
            log.info("오류 발생: {}", e.getMessage());
            redirect(UNAUTHORIZED, response);
        }
    }

    private User login(Map<String, String> userInfo) {
        String account = userInfo.get(ACCOUNT_KEY);
        String password = userInfo.get(PASSWORD_KEY);

        Optional<User> loginUser = InMemoryUserRepository.findByAccount(account);

        if (loginUser.isEmpty()) {
            throw new IllegalArgumentException(account + "는(은) 등록되지 않은 계정입니다.");
        }

        User user = loginUser.get();

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException(account + "의 비밀번호가 잘못 입력되었습니다.");
        }

        log.info("로그인 성공! user: {}", user);
        return user;
    }

    private void createNewSession(HttpRequest httpRequest, HttpResponse httpResponse, User user) {
        Session session = new Session(UUID.randomUUID().toString());

        if (httpRequest.existsSession() && sessionManager.findSession(httpRequest.getSession().getId()) != null) {
            session = httpRequest.getSession();
        }
        session.setAttribute(USER_KEY, user);
        sessionManager.add(session);

        httpResponse.setCookie(HttpCookie.setCookieHeader(session.getId()));
    }

    private Map<String, String> parseUserInfo(Map<String, String> requestBody) {
        Map<String, String> userInfo = new HashMap<>();
        if (requestBody.get(ACCOUNT_KEY).isEmpty() || requestBody.get(PASSWORD_KEY).isEmpty()) {
            throw new IllegalArgumentException("필수 입력값이 비어 있습니다.");
        }

        userInfo.put(ACCOUNT_KEY, requestBody.get(ACCOUNT_KEY));
        userInfo.put(PASSWORD_KEY, requestBody.get(PASSWORD_KEY));

        return userInfo;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            HttpCookies cookies = HttpCookies.from(request.getRequestHeader().getCookies());
            String id = cookies.getJsessionId();

            if (request.existsSession() && sessionManager.findSession(id) != null) {
                redirect(INDEX_PAGE, response);
                return;
            }
        } catch (NullPointerException e) {
            log.info("JSESSION 쿠키 값이 없습니다");
            redirect(LOGIN_PAGE, response);
        }
        redirect(LOGIN_PAGE, response);
    }
}
