package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.AbstractController;
import org.apache.catalina.StaticResource;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    private final StaticResource staticResource;

    public RegisterController(StaticResource staticResource) {
        this.staticResource = staticResource;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        response.setBody(staticResource.read("/register.html").orElse(new byte[0]));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        if (account != null && password != null && email != null) {
            InMemoryUserRepository.save(new User(account, password, email));
        }
        response.redirect("/index.html");
    }
}
