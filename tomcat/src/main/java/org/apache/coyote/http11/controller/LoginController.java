package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.manager.SessionManager;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryParameter;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.utils.Constants;
import org.apache.coyote.http11.utils.Cookies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String EXPRESSION_OF_USER = "user";

    @Override
    public HttpResponse process(HttpRequest request) {
        if (isLoginUser(request)) {
            HttpResponse response = new HttpResponse(HttpStatusCode.FOUND);
            response.sendRedirect(Constants.DEFAULT_URI);
            return response;
        }
        if (request.isGetMethod()) {
            return doGet(request);
        }
        if (request.isPostMethod()) {
            return doPost(request);
        }
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    private boolean isLoginUser(HttpRequest request) {
        if (request.hasJSessionCookie()) {
            Session session = getSession(request);
            return session.getAttribute(EXPRESSION_OF_USER) != null;
        }
        return false;
    }

    private Session getSession(HttpRequest request) {
        SessionManager sessionManager = SessionManager.getInstance();
        String jSessionId = request.getJSessionId();
        return sessionManager.findSession(jSessionId);
    }

    private HttpResponse doGet(HttpRequest request) {
        if (request.hasQueryParameter()) {
            printLogIfLoginPossible(request.getQueryParameter());
        }
        return new HttpResponse(HttpStatusCode.OK);
    }

    private void printLogIfLoginPossible(QueryParameter queryParameter) {
        try {
            User user = getUser(queryParameter);
            log.info("user : {}", user);
        } catch (IllegalArgumentException e) {
            log.info("잘못된 입력 : {}", e.getMessage(), e);
        }
    }

    private HttpResponse doPost(HttpRequest request) {
        try {
            User user = getUser(new QueryParameter(request.getBody()));
            log.info("로그인 성공! 아이디 : {}", user.getAccount());

            HttpResponse response = new HttpResponse(HttpStatusCode.FOUND);
            response.sendRedirect(Constants.DEFAULT_URI);
            addSessionCookie(request, response, user);
            return response;
        } catch (IllegalArgumentException e) {
            log.info("로그인 실패 : {}", e.getMessage(), e);
            HttpResponse response = new HttpResponse(HttpStatusCode.FOUND);
            response.sendRedirect("/401.html");
            return response;
        }
    }

    private void addSessionCookie(HttpRequest request, HttpResponse response, User user) {
        Session session = request.getSession(true);
        session.setAttribute(EXPRESSION_OF_USER, user);

        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        response.addCookie(Cookies.ofJSessionId(session.getId()));
    }

    private User getUser(QueryParameter queryParameter) {
        String account = queryParameter.get(Constants.PARAMETER_KEY_OF_ACCOUNT);
        String password = queryParameter.get(Constants.PARAMETER_KEY_OF_PASSWORD);

        return findUserByAccountAndPassword(account, password);
    }

    private User findUserByAccountAndPassword(String account, String password) {
        User user = findUserByAccount(account);
        if (user.checkPassword(password)) {
            return user;
        }
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다. 입력된 비밀번호: " + password);
    }

    private User findUserByAccount(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("입력된 아이디와 일치하는 유저를 찾을 수 없습니다. 입력된 아이디: " + account));
    }
}
