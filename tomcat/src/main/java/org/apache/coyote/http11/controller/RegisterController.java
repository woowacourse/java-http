package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        super.writeStaticResource(request, response, request.getPath() + ".html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        if (!request.hasBodyParameters("account", "password", "email")) {
            response.sendRedirect(request.getVersion(), "", "/401.html");
            return;
        }
        String account = request.getBodyParameter("account");
        String password = request.getBodyParameter("password");
        String email = request.getBodyParameter("email");
        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
        response.sendRedirect(request.getVersion(), "", "/index.html");
    }
}
