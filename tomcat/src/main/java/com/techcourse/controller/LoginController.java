package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public boolean isProvide(final String path) {
        return "/login".equals(path);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final HttpSession session = request.getSession();
        if (session == null) {
            response.setOk("login.html");
            return;
        }

        final Object user = session.getAttribute("user");
        if (user instanceof User) {
            response.setFound("index.html");
            return;
        }
        response.setOk("login.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final HttpSession oldSession = request.getSession();
        if (oldSession != null) {
            oldSession.invalidate();
        }

        final Map<String, String> bodyElement = request.getBodyElement();
        final String account = bodyElement.get("account");
        final String password = bodyElement.get("password");

        final Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);

        if (userOptional.isEmpty()) {
            log.warn("존재하지 않는 유저입니다: {}", account);
            response.setUnauthorized();
            return;
        }

        final User user = userOptional.get();

        if (!user.checkPassword(password)) {
            log.warn("유효하지 않는 password입니다.: {}", user.getAccount());
            response.setUnauthorized();
            return;
        }

        log.info("user : {}", user);

        response.setFound("index.html");
        String sessionId = UUID.randomUUID().toString();
        response.setCookie("JSESSIONID", sessionId);
        response.addAttribute("session_user", user);
    }
}
