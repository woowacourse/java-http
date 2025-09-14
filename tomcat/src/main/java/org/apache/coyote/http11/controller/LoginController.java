package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import java.util.Objects;
import org.apache.coyote.http11.Handler.StaticResourceHandler;
import org.apache.coyote.http11.model.Cookie;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;
import org.apache.coyote.http11.model.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        final var session = request.getSession();
        if (session != null && session.getAttribute("user") != null) {
            response.setStatusCode(StatusCode.FOUND);
            response.sendRedirect("/index.html");
            return;
        }
        response.setStatusCode(StatusCode.OK);
        response.setBodyAndContentLength(Objects.requireNonNull(
                StaticResourceHandler.getStaticResource(request.getPath())));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final var account = request.getQueryParameter("account");
        final var password = request.getQueryParameter("password");

        final var user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : {}", user);

            final var session = request.getSession();
            session.setAttribute("user", user.get());

            response.addCookie(Cookie.ofJSessionId(session.getId()));
            response.setStatusCode(StatusCode.FOUND);
            response.sendRedirect("/index.html");
            return;
        }

        response.setStatusCode(StatusCode.UNAUTHORIZED);
        response.setBodyAndContentLength(Objects.requireNonNull(
                StaticResourceHandler.getStaticResource("/401.html")));
    }
}
