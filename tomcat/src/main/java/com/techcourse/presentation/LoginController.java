package com.techcourse.presentation;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static LoginController INSTANCE;

    private final SessionManager sessionManager;

    private LoginController() {
        this.sessionManager = SessionManager.getInstance();
    }

    public static LoginController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LoginController();
        }
        return INSTANCE;
    }

    public void getLogin(Request request, Response response) {
        if (isAlreadyLoggedIn(request)) {
            response.configureViewAndStatus("/index", StatusCode.FOUND);
            return;
        }
        if (request.existQueryParams()) {
            String account = request.getQueryParamValue("account");
            String password = request.getQueryParamValue("password");
            login(account, password, response);
            return;
        }
        response.configureViewAndStatus("/login", StatusCode.OK);
    }

    private boolean isAlreadyLoggedIn(Request request) {
        Optional<String> sessionId = request.findJSessionId();
        Session session = sessionId.map(sessionManager::findSession).orElse(null);
        return session != null && session.getAttribute("user") != null;
    }

    public void postLogin(Request request, Response response) {
        String account = request.getBodyValue("account");
        String password = request.getBodyValue("password");
        login(account, password, response);
    }

    private void login(String account, String password, Response response) {
        try {
            User user = getUser(account, password);
            Session session = createSession(user);
            response.setJsessionid(session.getId());
            response.configureViewAndStatus("/index", StatusCode.FOUND);
            log.info("로그인 성공! 아이디 : {}", user.getAccount());
        } catch (IllegalArgumentException e) {
            response.configureViewAndStatus("/401", StatusCode.UNAUTHORIZED);
        }
    }

    private User getUser(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("계정 또는 비밀번호가 일치하지 않습니다."));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("계정 또는 비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    private Session createSession(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        sessionManager.add(session);
        return session;
    }
}
