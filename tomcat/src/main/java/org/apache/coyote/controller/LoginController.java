package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.IdGenerator;
import org.apache.coyote.util.RequestBodyParser;
import org.apache.coyote.view.StaticResourceView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (isUserAlreadyLoggedIn(request)) {
            response.sendRedirect("/index.html");
            return;
        }
        new StaticResourceView("login.html").render(response);
    }

    private boolean isUserAlreadyLoggedIn(HttpRequest request) {
        if (request.hasSession()) {
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.findSession(request.getSessionId());
            return session != null && session.getAttribute("user") != null;
        }
        return false;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> formData = RequestBodyParser.parseFormData(request.getBody());
        String account = formData.get("account");
        String password = formData.get("password");
        User user = authenticateUser(request, response, account, password);
        if (request.hasSession()) {
            createHasSessionUser(request, response, user);
        } else {
            createNoSessionUser(request, response, user);
        }
        log.info("{} - 회원 로그인 성공", user);
        response.sendRedirect("/index.html");
    }

    private User authenticateUser(HttpRequest request, HttpResponse response, String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseGet(() -> {
                    log.debug("{} - 존재하지 않는 회원의 로그인 요청", account);
                    response.updateHttpStatus(HttpStatus.UNAUTHORIZED);
                    throw new IllegalArgumentException("존재하지 않는 회원 입니다.");
                });
        validatePassword(request, response, user, password);
        return user;
    }

    private void validatePassword(HttpRequest request, HttpResponse response, User user, String password) {
        if (!user.checkPassword(password)) {
            log.debug("회원과 일치하지 않는 비밀번호 - 회원 정보 : {}, 입력한 비밀번호 {}", user, password);
            response.updateHttpStatus(HttpStatus.UNAUTHORIZED);
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
    }

    private void createHasSessionUser(HttpRequest request, HttpResponse response, User user) {
        SessionManager sessionManager = SessionManager.getInstance();
        String sessionId = request.getSessionId();
        Session session = sessionManager.findSession(sessionId);
        if (session == null) {
            String newSessionId = IdGenerator.generateUUID();
            response.addCookie("JSESSIONID", newSessionId);
            session = new Session(newSessionId);
            session.setAttribute("user", user);
            sessionManager.add(session);
            response.sendRedirect("/index.html");
        }
    }

    private void createNoSessionUser(HttpRequest request, HttpResponse response, User user) {
        SessionManager sessionManager = SessionManager.getInstance();
        String sessionId = IdGenerator.generateUUID();
        Session session = new Session(sessionId);
        sessionManager.add(session);
        response.addCookie("JSESSIONID", sessionId);
        session.setAttribute("user", user);
    }
}
