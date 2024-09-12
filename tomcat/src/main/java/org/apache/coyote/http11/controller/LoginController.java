package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private static final String USER_SESSION_INFO_NAME = "user";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        HttpCookies cookies = request.getCookies();
        String sessionId = cookies.getSessionId();
        if (!sessionId.isEmpty()) {
            findUserFromSession(sessionId, response);
        }
        QueryParam queryParam = request.getQueryParam();
        loginCheck(queryParam, response);
    }

    private void findUserFromSession(String sessionId, HttpResponse response) {
        Session session = SessionManager.getInstance().findSessionById(sessionId);
        User user = (User) session.findValue(USER_SESSION_INFO_NAME);
        logger.info("session user : {}", user);
        response.statusCode(HttpStatusCode.FOUND)
                .staticResource("/index.html");
    }

    private void loginCheck(QueryParam queryParam, HttpResponse response) {
        if (queryParam.getValue("account").isEmpty() &&
                queryParam.getValue("password").isEmpty()) {
            response.statusCode(HttpStatusCode.OK)
                    .staticResource("/login.html");
        }
        login(queryParam, response);
    }

    private void login(QueryParam queryParam, HttpResponse response) {
        String account = queryParam.getValue("account");
        String password = queryParam.getValue("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("계정 정보가 존재하지 않습니다."));
        if (user.checkPassword(password)) {
            loginSuccess(user, response);
        }
        loginFail(response);
    }

    private void loginSuccess(User account, HttpResponse response) {
        response.statusCode(HttpStatusCode.FOUND)
                .createSession(USER_SESSION_INFO_NAME, account)
                .redirect("index.html");
    }

    private void loginFail(HttpResponse response) {
        response.statusCode(HttpStatusCode.UNAUTHORIZED)
                .staticResource("/401.html");
    }
}
