package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.List;
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
        if (SessionManager.getInstance().getSession(request) != null) {
            processSessionLogin(responseBuilder);
            return;
        }
        if (request.hasParameters(List.of("account", "password"))) {
            processAccountLogin(request, responseBuilder);
            return;
        }
        resourceController.doGet(request.updatePath("login.html"), responseBuilder);
    }

    private void processSessionLogin(Builder responseBuilder) {
        responseBuilder.status(Status.FOUND)
                .location("/index.html");
    }

    private void processAccountLogin(HttpRequest request, HttpResponse.Builder responseBuilder) {
        String account = request.parameters().get("account");
        String password = request.parameters().get("password");

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> processLoginSuccess(responseBuilder, user),
                        () -> responseBuilder.status(Status.FOUND).location("/401.html"));
    }

    private void processLoginSuccess(Builder responseBuilder, User user) {
        HttpSession session = SessionManager.getInstance().createSession(user);

        log.info("로그인 성공! - 아이디 : {}, 세션 ID : {}", user.getAccount(), session.getId());

        responseBuilder.status(Status.FOUND)
                .location("/index.html")
                .addCookie(JSession.COOKIE_NAME, session.getId());
    }
}
