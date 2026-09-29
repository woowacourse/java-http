package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final StaticResourceController staticResources;

    public LoginController(final StaticResourceController staticResources) {
        this.staticResources = staticResources;
    }

    @Override
    protected void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) throws Exception {
        final Session session = request.getSession();

        if (session != null && session.getAttribute("user") != null) {
            redirect(response, "/index.html");
            return;
        }

        staticResources.render("/login.html", response);
    }

    @Override
    protected void doPost(
            final HttpRequest request,
            final HttpResponse response
    ) {
        final Map<String, String> parameters = request.getFormParameters();
        final String account = parameters.get("account");
        final String password = parameters.get("password");

        if (account == null || password == null) {
            redirect(response, "/401.html");
            return;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(found -> found.checkPassword(password));

        if (user.isEmpty()) {
            redirect(response, "/401.html");
            return;
        }

        log.info("로그인한 회원: {}", account);
        request.getOrCreateSession(response).setAttribute("user", user.get());
        redirect(response, "/index.html");
    }

    private void redirect(final HttpResponse response, final String location) {
        response.setStatus(302, "Found");
        response.addHeader("Location", location);
    }
}
