package com.techcourse.controller;

import com.techcourse.auth.Session;
import com.techcourse.auth.SessionManager;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.http.MimeType;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.StaticResourceProvider;
import org.apache.coyote.http11.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String JSESSIONID = "JSESSIONID";

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String jSession = request.getCookie(JSESSIONID);
        if (request.hasNotParameters() && jSession == null) {
            response.setBody(StaticResourceProvider.getStaticResource("/login.html"))
                    .setContentType(MimeType.HTML.getMimeType());
            return;
        }

        if (request.hasNotParameters()) {
            response.found("/index.html");
            return;
        }

        if (request.hasNotParameter("account") || request.hasNotParameter("password")) {
            response.found("/401.html");
            return;
        }

        String account = request.getParameter("account");
        String password = request.getParameter("password");
        Optional<User> userOpt = InMemoryUserRepository.findByAccount(account);
        if (userOpt.isEmpty() || !userOpt.get().checkPassword(password)) {
            response.found("/401.html");
            return;
        }
        User user = userOpt.get();

        if (jSession == null) {
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user);
            sessionManager.add(session);
            response.setCookie(JSESSIONID, session.getId());
        }

        response.found("/index.html");
        log.info("user : {}", user);
    }
}
