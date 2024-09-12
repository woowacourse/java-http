package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPageController extends Controller {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private static final String USER_SESSION_INFO_NAME = "user";

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        HttpCookies cookies = request.getCookies();
        String sessionId = cookies.getSessionId();
        if (!sessionId.isEmpty()) {
            return findUserFromSession(sessionId);
        }
        QueryParam queryParam = request.getQueryParam();
        return loginCheck(queryParam);
    }

    private HttpResponse findUserFromSession(String sessionId) {
        Session session = SessionManager.getInstance().findSessionById(sessionId);
        User user = (User) session.findValue(USER_SESSION_INFO_NAME);
        logger.info("session user : {}", user);
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.FOUND)
                .staticResource("/index.html");
    }

    private HttpResponse loginCheck(QueryParam queryParam) {
        if (queryParam.getValue("account").isEmpty() &&
                queryParam.getValue("password").isEmpty()) {
            return HttpResponse.builder()
                    .statusCode(HttpStatusCode.OK)
                    .staticResource("/login.html");
        }
        return login(queryParam);
    }

    private HttpResponse login(QueryParam queryParam) {
        String account = queryParam.getValue("account");
        String password = queryParam.getValue("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("계정 정보가 존재하지 않습니다."));
        if (user.checkPassword(password)) {
            return loginSuccess(user);
        }
        return loginFail();
    }

    private HttpResponse loginSuccess(User account) {
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.FOUND)
                .createSession(USER_SESSION_INFO_NAME, account)
                .redirect("index.html");
    }

    private HttpResponse loginFail() {
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.UNAUTHORIZED)
                .staticResource("/401.html");
    }
}
