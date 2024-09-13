package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.manager.SessionManager;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.utils.Constants;
import org.apache.coyote.http11.utils.Cookies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String EXPRESSION_OF_USER = "user";

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            String account = request.getBodyAttribute(Constants.PARAMETER_KEY_OF_ACCOUNT);
            String password = request.getBodyAttribute(Constants.PARAMETER_KEY_OF_PASSWORD);

            User user = getUserIfLoginPossible(account, password);
            login(user, request, response);
        } catch (IllegalArgumentException e) {
            log.info("로그인 실패 : {}", e.getMessage(), e);
            response.sendRedirect(UNAUTHORIZED_PAGE);
        }
    }

    private User getUserIfLoginPossible(String account, String password) {
        User user = findUserByAccount(account);
        return authenticateUser(user, password);
    }

    private User findUserByAccount(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("입력된 아이디와 일치하는 유저를 찾을 수 없습니다. 입력된 아이디: " + account));
    }

    private User authenticateUser(User user, String password) {
        if (user.checkPassword(password)) {
            return user;
        }
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다. 입력된 비밀번호: " + password);
    }

    private void login(User user, HttpRequest request, HttpResponse response) {
        log.info("로그인 성공! 아이디 : {}", user.getAccount());

        response.sendRedirect(Constants.DEFAULT_URI);
        addSessionCookie(request, response, user);
    }

    private void addSessionCookie(HttpRequest request, HttpResponse response, User user) {
        Session session = request.getSession(true);
        session.setAttribute(EXPRESSION_OF_USER, user);

        sessionManager.add(session);
        response.setCookie(Cookies.ofJSessionId(session.getId()));
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (isLoginUser(request)) {
            response.sendRedirect(Constants.DEFAULT_URI);
            return;
        }
        if (request.hasQueryParameter()) {
            printLogIfLoginPossible(request);
        }
    }

    private boolean isLoginUser(HttpRequest request) {
        if (request.hasJSessionCookie()) {
            String jSessionId = request.getJSessionId();
            Session session = sessionManager.findSession(jSessionId);
            return session.getAttribute(EXPRESSION_OF_USER) != null;
        }
        return false;
    }

    private void printLogIfLoginPossible(HttpRequest request) {
        try {
            String account = request.getQueryParameterAttribute(Constants.PARAMETER_KEY_OF_ACCOUNT);
            String password = request.getQueryParameterAttribute(Constants.PARAMETER_KEY_OF_PASSWORD);

            User user = getUserIfLoginPossible(account, password);
            log.info("user : {}", user);
        } catch (IllegalArgumentException e) {
            log.info("잘못된 입력 : {}", e.getMessage(), e);
        }
    }
}
