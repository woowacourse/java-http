package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.request.RequestParams.anyBlank;

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

        if (anyBlank(account, password, email) || isDuplicateAccount(account)) {
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

    private boolean isDuplicateAccount(String account) {
        return InMemoryUserRepository.findByAccount(account).isPresent();
    }
}
