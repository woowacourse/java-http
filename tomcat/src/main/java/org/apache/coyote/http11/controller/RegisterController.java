package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.StaticResourceResolver;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response, HttpSession session) throws Exception {
        final var responseBody = StaticResourceResolver.read(request.getPath());
        response.sendOk("text/html", responseBody);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response, HttpSession session) throws Exception {
        final var newUser = new User(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        );
        InMemoryUserRepository.save(newUser);
        response.sendRedirect("/index.html");
    }
}
