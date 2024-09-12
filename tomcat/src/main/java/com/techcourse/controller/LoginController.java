package com.techcourse.controller;

import static com.techcourse.controller.PagePath.INDEX_PAGE;
import static com.techcourse.controller.PagePath.LOGIN_PAGE;
import static com.techcourse.controller.PagePath.UNAUTHORIZED_PAGE;
import static org.apache.coyote.http11.HttpCookie.JSESSIONID;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.MissingRequestBodyException;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.was.Controller.AbstractController;
import org.was.Controller.ResponseResult;
import org.was.session.Session;
import org.was.session.SessionManager;

public class LoginController extends AbstractController {

    private static final String USER_ATTRIBUTE = "user";

    @Override
    protected ResponseResult doPost(HttpRequest request) {
        Map<String, String> requestFormData = request.getFormData();
        if (requestFormData.isEmpty()) {
            return responseMissingBody();
        }
        String userName = requestFormData.get("account");
        String password = requestFormData.get("password");

        Optional<User> account = InMemoryUserRepository.findByAccount(userName);

        if (account.isEmpty()) {
            return responseLoginFail();
        }

        User user = account.get();
        if (user.checkPassword(password)) {
            return responseLoginSuccess(user);
        } else {
            return responseLoginFail();
        }
    }

    private ResponseResult responseLoginSuccess(User user) {
        Session session = createSession(user);
        String cookieValue = createSessionCookie(session);

        return ResponseResult
                .status(HttpStatusCode.FOUND)
                .header(HttpHeaders.LOCATION.getName(), INDEX_PAGE.getPath())
                .header(HttpHeaders.COOKIE.getName(), cookieValue)
                .build();
    }

    private Session createSession(User user) {
        Session session = new Session();
        session.setAttribute(USER_ATTRIBUTE, user);
        SessionManager.add(session);
        return session;
    }

    private String createSessionCookie(Session session) {
        HttpCookie cookie = HttpCookie.ofJSessionId(session.getId());
        return JSESSIONID + "=" + cookie.getJSessionId();
    }

    @Override
    protected ResponseResult doGet(HttpRequest request) {
        if (checkLoginSession(request)) {
            return responseRedirectIndex();
        }

        return responseLoginPage();
    }

    private boolean checkLoginSession(HttpRequest request) {
        HttpCookie cookie = request.getCookie();
        if (cookie == null || cookie.getJSessionId() == null) {
            return false;
        }

        String jSessionId = cookie.getJSessionId();
        Session session = SessionManager.findSession(jSessionId);
        return session != null && session.hasAttribute(USER_ATTRIBUTE);
    }

    private ResponseResult responseMissingBody() {
        return ResponseResult
                .status(HttpStatusCode.BAD_REQUEST)
                .body(new MissingRequestBodyException().getMessage());
    }

    private ResponseResult responseLoginFail() {
        return ResponseResult
                .status(HttpStatusCode.UNAUTHORIZED)
                .header(HttpHeaders.LOCATION.getName(), UNAUTHORIZED_PAGE.getPath())
                .build();
    }

    private ResponseResult responseRedirectIndex() {
        return ResponseResult
                .status(HttpStatusCode.FOUND)
                .header(HttpHeaders.LOCATION.getName(), INDEX_PAGE.getPath())
                .build();
    }

    private ResponseResult responseLoginPage() {
        return ResponseResult
                .status(HttpStatusCode.OK)
                .path(LOGIN_PAGE.getPath());
    }
}
