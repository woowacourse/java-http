package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UserException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final LoginController instance = new LoginController();

    private static final String LOGIN_ACCOUNT_KEY = "account";

    private static final String LOGIN_PASSWORD_KEY = "password";

    private static final String INDEX_PAGE = "/index.html";

    private static final String UNAUTHORIZED_PAGE = "/401.html";

    private LoginController() {
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        String account = request.getRequestBodyValue(LOGIN_ACCOUNT_KEY);
        String password = request.getRequestBodyValue(LOGIN_PASSWORD_KEY);
        try {
            User foundUser = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new UserException(account + "는 존재하지 않는 계정입니다."));
            loginProcess(password, foundUser, response);
        } catch (UserException e) {
            setFailResponse(request, response);
        }
        setResponseContent(request, response);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        if (checkLogin(request)) {
            redirect(response, INDEX_PAGE);
        }
        setResponseContent(request, response);
    }

    private void loginProcess(String password, User user, HttpResponse response) {
        if (user.checkPassword(password)) {
            log.info("user : " + user);
            response.setHttpStatusCode(HttpStatusCode.FOUND);
            redirect(response, INDEX_PAGE);
            setSessionAtResponseHeader(user, response);
        }
    }

    private boolean checkLogin(HttpRequest request) {
        HttpCookie httpCookie = request.getHttpCookie();
        String jsessionid = httpCookie.getJsessionid();
        if (jsessionid.isEmpty()) {
            return false;
        }
        return SessionManager.containsSession(jsessionid);
    }

    private void setSessionAtResponseHeader(User user, HttpResponse response) {
        String jsessionid = UUID.randomUUID().toString();
        Session session = new Session(jsessionid);
        session.setAttribute("user", user);
        SessionManager.add(session.getId(), session);
        response.setHttpResponseHeader("Set-Cookie", "JSESSIONID=" + jsessionid);
    }

    private void setFailResponse(HttpRequest request, HttpResponse response) {
        request.setHttpRequestPath(UNAUTHORIZED_PAGE);
        response.setHttpStatusCode(HttpStatusCode.UNAUTHORIZED);
    }

    public static LoginController getInstance() {
        return instance;
    }
}
