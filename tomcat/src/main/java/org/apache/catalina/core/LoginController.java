package org.apache.catalina.core;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.catalina.mapping.AbstractController;
import org.apache.coyote.util.request.HttpRequest;
import org.apache.coyote.util.response.HttpResponse;
import org.apache.coyote.util.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        Session existingSession = request.getSession(false);
        if (existingSession != null && existingSession.getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }
        response.setStatus(HttpStatus.OK);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody(StaticResourceHandler.readResource("static/login.html"));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Session existingSession = request.getSession(false);
        if (existingSession != null && existingSession.getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }

        String account = request.getBody().get("account");
        String password = request.getBody().get("password");

        Optional<User> userOpt = InMemoryUserRepository.findByAccount(account);
        if (userOpt.isEmpty() || !userOpt.get().checkPassword(password)) {
            response.sendRedirect("/401.html");
            return;
        }

        User user = userOpt.get();
        final Session session = request.changeSessionId();
        session.setAttribute("user", user);

        response.sendRedirect("/index.html");
        response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; Path=/");
        log.info("User: {}", user);
    }
}
