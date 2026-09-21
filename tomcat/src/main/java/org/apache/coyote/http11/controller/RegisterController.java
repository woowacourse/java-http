package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.resolver.View;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getBodyParameter("account");
        String password = request.getBodyParameter("password");
        String email = request.getBodyParameter("email");

        if (isBlank(account) || isBlank(password) || isBlank(email)
                || InMemoryUserRepository.findByAccount(account).isPresent()) {
            response.sendRedirect("/register.html");
            return;
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        response.sendRedirect("/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        View.renderStaticPage(request.getUri(), response);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
