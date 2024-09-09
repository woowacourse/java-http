package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.JSession;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponse.Builder;
import org.apache.coyote.http11.Status;

public class LoginController extends AbstractController {

    private final ResourceController resourceController;

    public LoginController() {
        this.resourceController = new ResourceController();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse.Builder responseBuilder) {
        HttpSession session = SessionManager.getInstance().getSession(request);
        if (session != null) {
            processSessionLogin(responseBuilder);
            return;
        }

        if (request.hasParameters(List.of("account", "password"))) {
            findValidUser(request).ifPresentOrElse(
                    user -> processAccountLogin(responseBuilder, user),
                    () -> responseBuilder.status(Status.FOUND).location("/401.html")
            );
            return;
        }

        resourceController.doGet(request.updatePath("login.html"), responseBuilder);
    }

    private void processSessionLogin(Builder responseBuilder) {
        responseBuilder.status(Status.FOUND)
                .location("/index.html");
    }

    private Optional<User> findValidUser(HttpRequest request) {
        String account = request.parameters().get("account");
        String password = request.parameters().get("password");

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private void processAccountLogin(Builder responseBuilder, User user) {
        String sessionId = UUID.randomUUID().toString();
        JSession jSession = new JSession(sessionId);
        jSession.setAttribute("user", user);
        SessionManager.getInstance().add(jSession);

        log.info("로그인 성공! - 아이디 : {}, 세션 ID : {}", user.getAccount(), sessionId);

        responseBuilder.status(Status.FOUND)
                .location("/index.html")
                .addCookie(JSession.COOKIE_NAME, sessionId);
    }
}
