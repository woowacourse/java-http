package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.MappedController;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginController extends AbstractController implements MappedController {

    private final StaticResourceController resourceController = new StaticResourceController();

    @Override
    public String getPath() {
        return "/login";
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        Session session = request.findSession();
        if (session != null && session.getAttribute("user") != null) {
            HttpResponse response = new HttpResponse();
            response.redirect("/index.html");
            return response;
        }
        return resourceController.serve("/login.html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getBody().get("account");
        String password = request.getBody().get("password");
        Optional<User> user = InMemoryUserRepository.findByAccountAndPassword(account, password);
        HttpResponse response = new HttpResponse();
        if (user.isPresent()) {
            request.getOrCreateSession().setAttribute("user", user.get());
            response.redirect("/index.html");
            return response;
        }

        response.redirect("/401.html");
        return response;
    }
}
