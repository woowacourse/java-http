package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.request.RequestParams.anyBlank;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.resolver.View;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.catalina.session.Session;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException, URISyntaxException {
        Session session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }
        View.renderStaticPage(request.getUri(), response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getBodyParameter("account");
        String password = request.getBodyParameter("password");
        if (anyBlank(account, password)) {
            response.sendRedirect("/401.html");
            return;
        }
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
        if (optionalUser.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }
        login(request, response, optionalUser.get());
        response.sendRedirect("/index.html");
    }

    private static void login(HttpRequest request, HttpResponse response, User user) {
        final var session = request.getSession(true);
        session.setAttribute("user", user);
        response.addCookie(Cookie.ofJSessionId(session.getId()));
    }
}
