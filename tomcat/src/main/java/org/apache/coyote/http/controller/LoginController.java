package org.apache.coyote.http.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.session.Session;
import org.apache.coyote.http.cookie.HttpCookie;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        Session session = request.getSession(false);
        if (session != null && getUser(session) != null) {
            return HttpResponse.redirect("/index.html");
        }

        return handleStaticFile("/login.html", "text/html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        final var params = request.parseFormData();
        final var account = params.get("account");
        final var password = params.get("password");

        final var user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            final var session = request.getSession(true);
            session.setAttribute("user", user.get());
            return HttpResponse.redirectWithCookie("/index.html", HttpCookie.JSESSIONID, session.getId());
        } else {
            try {
                final var path = Path.of(getClass().getResource("/static/401.html").getPath());
                final var content = new String(Files.readAllBytes(path));
                return HttpResponse.unauthorized(content);
            } catch (Exception e) {
                return HttpResponse.unauthorized("401 Unauthorized");
            }
        }
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }
}
