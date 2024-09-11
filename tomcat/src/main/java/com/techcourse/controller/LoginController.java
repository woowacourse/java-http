package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.LoginCredentials;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.Handler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseFile;

public class LoginController extends AbstractController {

    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String SESSION_KEY = "user";

    public LoginController() {
        List<Handler> handlers = List.of(
                Handler.ofGet(LOGIN_PATH, this::doLoginGet),
                Handler.ofPost(LOGIN_PATH, this::doLoginPost)
        );
        registerHandlers(handlers);
    }

    private void doLoginPost(HttpRequest request, HttpResponse response) {
        LoginCredentials loginCredentials = createLoginCredentials(request);
        User user = getUser(loginCredentials);

        HttpSession session = request.getSession();
        session.setAttribute(SESSION_KEY, user);

        response.redirectTo("/index.html");
    }

    private LoginCredentials createLoginCredentials(HttpRequest request) {
        String account = request.getBodyParameter(ACCOUNT);
        String password = request.getBodyParameter(PASSWORD);

        return new LoginCredentials(account, password);
    }

    private User getUser(LoginCredentials loginCredentials) {
        String account = loginCredentials.getAccount();
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));

        if (!user.checkPassword(loginCredentials)) {
            throw new UnauthorizedException();
        }
        return user;
    }

    private void doLoginGet(HttpRequest request, HttpResponse response) {
        if (isLoginUser(request)) {
            response.redirectTo("/index.html");
            return;
        }
        ResponseFile responseFile = ResponseFile.of(LOGIN_PAGE);
        response.setHttpStatus(HttpStatus.OK);
        response.addFile(responseFile);
    }

    private boolean isLoginUser(HttpRequest request) {
        HttpSession requestSession = request.getSession();
        return requestSession.getAttribute(SESSION_KEY) != null;
    }
}
