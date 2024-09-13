package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.apache.catalina.session.JSession;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponse.Builder;
import org.apache.coyote.http11.response.Status;

public class LoginController extends AbstractController {

    private final ResourceController resourceController;

    public LoginController() {
        this.resourceController = new ResourceController();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse.Builder responseBuilder) {
        if (SessionManager.getInstance().getSession(request) != null) {
            responseBuilder.status(Status.FOUND)
                    .location("/index.html");
            return;
        }
        resourceController.doGet(request.updatePath("login.html"), responseBuilder);
    }

    @Override
    protected void doPost(HttpRequest request, Builder responseBuilder) {
        Map<String, String> body = request.extractUrlEncodedBody();
        if (isInvalidBody(body)) {
            responseBuilder.status(Status.FOUND)
                    .location("/login");
            return;
        }
        processAccountLogin(body, responseBuilder);
    }

    private boolean isInvalidBody(Map<String, String> body) {
        return !body.containsKey("account") ||
                !body.containsKey("password");
    }

    private void processAccountLogin(Map<String, String> body, HttpResponse.Builder responseBuilder) {
        String account = body.get("account");
        String password = body.get("password");

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
