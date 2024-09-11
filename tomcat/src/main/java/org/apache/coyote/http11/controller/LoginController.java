package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_PATH = "/login";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String INDEX_PATH = "/index.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_DELIMITER = "=";
    private static final String SESSION_USER_NAME = "user";

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        if (validateUserInput(httpRequest)) {
            log.error("입력하지 않은 항목이 있습니다.");
            return redirectPage(httpRequest, LOGIN_PATH);
        }
        return acceptLogin(httpRequest);
    }

    private boolean validateUserInput(HttpRequest httpRequest) {
        return !httpRequest.containsBody(ACCOUNT) || !httpRequest.containsBody(PASSWORD);
    }

    private HttpResponse acceptLogin(HttpRequest httpRequest) {
        String account = httpRequest.getBodyValue(ACCOUNT);
        String password = httpRequest.getBodyValue(PASSWORD);

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        if (user.checkPassword(password)) {
            return redirectWithCookie(httpRequest, user);
        }
        log.error("비밀번호 불일치");
        return redirectPage(httpRequest, UNAUTHORIZED_PATH);
    }

    private static HttpResponse redirectWithCookie(HttpRequest httpRequest, User user) {
        Session session = httpRequest.getSession();
        session.setAttribute(SESSION_USER_NAME, user);
        log.info(user.toString());
        return HttpResponse.found(httpRequest)
                .setCookie(JSESSIONID + COOKIE_DELIMITER + session.getId())
                .location(INDEX_PATH)
                .build();
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        Session session = httpRequest.getSession();
        if (!session.hasAttribute(SESSION_USER_NAME)) {
            return HttpResponse.ok(httpRequest)
                    .staticResource(LOGIN_PATH)
                    .build();
        }
        User user = (User) session.getAttribute(SESSION_USER_NAME);
        log.info(user.toString());
        return HttpResponse.found(httpRequest)
                .setCookie(JSESSIONID + COOKIE_DELIMITER + session.getId())
                .location(INDEX_PATH)
                .build();
    }

    private HttpResponse redirectPage(HttpRequest httpRequest, String path) {
        return HttpResponse.found(httpRequest)
                .location(path)
                .build();
    }
}
