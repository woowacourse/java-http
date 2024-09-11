package com.techcourse.servlet.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.servlet.Handler;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private static final HttpMethod METHOD = HttpMethod.GET;
    private static final String PATH = "/login";
    private static final View UNAUTHORIZED_VIEW = View.htmlBuilder()
            .status(HttpStatus.UNAUTHORIZED)
            .staticResource("/401.html")
            .build();

    private final SessionManager sessionManager;

    public LoginHandler() {
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    public boolean support(HttpRequest request) {
        return request.getPath().equals(PATH)
                && request.getMethod() == METHOD
                && !request.getQueryParameters().isEmpty();
    }

    @Override
    public View handle(HttpRequest request) {
        Optional<String> account = request.getQueryParameter("account");
        Optional<String> password = request.getQueryParameter("password");
        if (account.isPresent() && password.isPresent()) {
            leaveUserLog(account.get(), password.get());
            return handle(account.get(), password.get());
        }
        return UNAUTHORIZED_VIEW;
    }

    private void leaveUserLog(String account, String password) {
        InMemoryUserRepository.findByAccountAndPassword(account, password)
                .ifPresentOrElse(
                        user -> log.info("user : {}", user),
                        () -> log.info("해당 유저가 존재하지 않습니다"));
    }

    private View handle(String account, String password) {
        return InMemoryUserRepository.findByAccountAndPassword(account, password)
                .map(this::handleLoginSuccess)
                .orElse(UNAUTHORIZED_VIEW);
    }

    private View handleLoginSuccess(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        sessionManager.add(session);

        return View.redirectBuilder()
                .addHeader("Set-Cookie", "JSESSIONID=" + session.getId())
                .location("/index.html")
                .build();
    }
}
