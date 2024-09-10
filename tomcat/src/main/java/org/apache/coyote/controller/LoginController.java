package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.IdGenerator;
import org.apache.coyote.util.RequestBodyParser;
import org.apache.coyote.http11.Session;
import org.apache.coyote.util.SessionManager;
import org.apache.coyote.util.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (request.hasSession()) {
            String sessionId = request.getSessionId();
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.findSession(sessionId);
            User user = session.getUser();
            redirectHomeAuthUser(response, user);
            return;
        }
        ViewResolver.resolveView("login.html", response);
    }

    private void redirectHomeAuthUser(HttpResponse response, User user) {
        if (user != null) {
            response.sendRedirect("/index.html");
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> formData = RequestBodyParser.parseFormData(request.getBody());
        String account = formData.get("account");
        String password = formData.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseGet(() -> {
                    log.debug("{} - 존재하지 않는 회원의 로그인 요청", account);
                    response.updateHttpStatus(HttpStatus.UNAUTHORIZED);
                    throw new IllegalArgumentException("존재하지 않는 회원 입니다.");
                });
        validatePassword(request, response, user, password);
        log.info("{} - 회원 로그인 성공", user);

        if (!request.hasSession()) {
            String sessionId = IdGenerator.generateUUID();
            response.addCookie("JSESSIONID", sessionId);
            Session session = new Session(sessionId);
            session.setUser(user);
            SessionManager sessionManager = SessionManager.getInstance();
            sessionManager.add(session);
        }
        response.sendRedirect("/index.html");
    }

    private void validatePassword(HttpRequest request, HttpResponse response, User user, String password) {
        if (!user.checkPassword(password)) {
            log.debug("회원과 일치하지 않는 비밀번호 - 회원 정보 : {}, 입력한 비밀번호 {}", user, password);
            response.updateHttpStatus(HttpStatus.UNAUTHORIZED);
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
    }
}
