package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryParams;
import org.apache.coyote.http11.resolver.View;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        QueryParams params = request.getBody().getQueryParams();
        String account = params.getValue("account");
        String password = params.getValue("password");
        String email = params.getValue("email");
        if (isBlank(account) || isBlank(password) || isBlank(email)) {
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
