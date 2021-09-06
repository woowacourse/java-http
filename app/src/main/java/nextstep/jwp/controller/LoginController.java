package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.session.HttpSession;
import nextstep.jwp.session.HttpSessions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nextstep.jwp.PageUrl.*;

public class LoginController extends AbstractController {
    public static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (request.hasCookie() && isLoginStatus(request.getSession())) {
            response.redirect(INDEX_PAGE.getPath());
            return;
        }
        response.forward(LOGIN_PAGE.getPath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.getRequestBodyParam("account");
        String password = request.getRequestBodyParam("password");

        User user = getUser(response, account);

        if (!user.checkPassword(password)) {
            response.redirect(UNAUTHORIZED_PAGE.getPath());
            return;
        }

        if (request.hasNoSessionId()) {
            String sessionId = String.valueOf(UUID.randomUUID());
            HttpSession session = HttpSessions.getSession(sessionId);
            session.setAttribute("user", user);
            response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId);
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
        }
        response.redirect(INDEX_PAGE.getPath());
    }

    private User getUser(HttpResponse response, String account) {
        return InMemoryUserRepository.findByAccount(account).orElseGet(() -> {
            try {
                response.redirect(INDEX_PAGE.getPath());
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        });
    }

    private boolean isLoginStatus(HttpSession session) {
        Object user = session.getAttributes("user");
        return Objects.nonNull(user);
    }
}

