package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.JSession;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Status;

public class LoginController extends AbstractController {

    private final SessionManager sessionManager;
    private final ResourceController resourceController;

    public LoginController() {
        this.sessionManager = new SessionManager();
        this.resourceController = new ResourceController();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse.Builder responseBuilder) {
        if (request.cookies().containsKey(JSession.COOKIE_NAME)) {
            HttpSession session = request.getSession(sessionManager);
            if (session != null) {
                User user = (User) Objects.requireNonNull(session).getAttribute("user");

                log.info("세션 로그인 성공! - 아이디 : {}, 세션 ID : {}", user.getAccount(), session.getId());

                responseBuilder.status(Status.FOUND)
                        .location("/index.html");
                return;
            }
        }

        if (request.parameters().containsKey("account") && request.parameters().containsKey("password")) {
            String account = request.parameters().get("account");
            String password = request.parameters().get("password");

            Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
            if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
                User user = optionalUser.get();
                String sessionId = UUID.randomUUID().toString();
                JSession session = new JSession(sessionId);
                session.setAttribute("user", user);
                sessionManager.add(session);

                log.info("계정 정보 로그인 성공! - 아이디 : {}, 세션 ID : {}", user.getAccount(), sessionId);

                responseBuilder.status(Status.FOUND)
                        .location("/index.html")
                        .addCookie(JSession.COOKIE_NAME, sessionId);
                return;
            }

            responseBuilder.status(Status.FOUND)
                    .location("/401.html");
            return;
        }

        resourceController.doGet(request.updatePath("login.html"), responseBuilder);
    }
}
