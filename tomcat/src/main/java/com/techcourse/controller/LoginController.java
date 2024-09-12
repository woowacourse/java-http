package com.techcourse.controller;

import java.util.UUID;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.exception.UnauthorizedException;
import org.apache.http.HttpCookie;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {

    private static final LoginController INSTANCE = new LoginController();
    private final SessionManager sessionManager = SessionManager.getInstance();

    private LoginController() {
    }

    public static LoginController getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final String account = request.getFormBodyByKey("account");
        final String requestPassword = request.getFormBodyByKey("password");
        final User savedUser = getvaliduser(account, requestPassword);
        validateUserPassword(savedUser, requestPassword);

        response.setResponse(processSuccessLogin(savedUser));
    }

    private User getvaliduser(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 유저입니다."));
    }

    private void validateUserPassword(final User user, final String password) {
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("로그인에 실패하였습니다.");
        }
    }

    private HttpResponse processSuccessLogin(final User user) {
        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        sessionManager.add(session);

        return HttpResponse.builder()
                .addCookie("JSESSIONID", session.getId())
                .foundBuild("/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        HttpCookie httpCookie = request.getHttpCookie();
        if (httpCookie == null || hasNotValidJSessionId(httpCookie)) {
            response.setResponse(HttpResponse.builder().foundBuild("/login.html"));
            return;
        }

        response.setResponse(HttpResponse.builder().foundBuild("/index.html"));
    }

    private boolean hasNotValidJSessionId(HttpCookie httpCookie) {
        String value = httpCookie.getValue("JSESSIONID");
        return !sessionManager.existsById(value);
    }
}
