package com.techcourse.web.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestParameters;
import org.apache.coyote.http11.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * /login 요청을 처리함. 로그인 페이지를 응답하고, 인증에 성공하면 세션에 사용자를 저장함.
 */
public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    private static final String USER_ATTRIBUTE = "user";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Session session = request.getSession(false);
        if (isLoggedIn(session)) {
            response.sendRedirect(INDEX_PAGE);
            return;
        }

        Optional<String> body = ResourceLoader.read(LOGIN_PAGE);
        if (body.isEmpty()) {
            renderNotFound(response);
            return;
        }
        response.ok(ResourceLoader.contentTypeOf(LOGIN_PAGE), body.get());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Optional<User> user = login(request.getFormData());
        if (user.isEmpty()) {
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }

        request.getSession(true).setAttribute(USER_ATTRIBUTE, user.get());
        response.sendRedirect(INDEX_PAGE);
    }

    private boolean isLoggedIn(Session session) {
        return session != null && session.getAttribute(USER_ATTRIBUTE) != null;
    }

    private Optional<User> login(RequestParameters formData) {
        Optional<String> account = formData.get("account");
        Optional<String> password = formData.get("password");
        if (account.isEmpty() || password.isEmpty()) {
            return Optional.empty();
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account.get());
        if (user.isPresent() && user.get().checkPassword(password.get())) {
            log.info("회원 조회 결과: user={}", user.get());
            return user;
        }
        return Optional.empty();
    }
}
