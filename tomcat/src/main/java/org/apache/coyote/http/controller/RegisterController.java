package org.apache.coyote.http.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http.cookie.HttpCookie;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return handleStaticFile("/register.html", "text/html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        final var params = request.parseFormData();
        final var account = params.get("account");
        final var password = params.get("password");
        final var email = params.get("email");

        if (account == null || password == null || email == null) {
            return handleStaticFile("/register.html", "text/html");
        }

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return handleStaticFile("/register.html", "text/html");
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        final var session = request.getSession(true);
        session.setAttribute("user", user);

        return HttpResponse.redirectWithCookie("/index.html", HttpCookie.JSESSIONID, session.getId());
    }
}
