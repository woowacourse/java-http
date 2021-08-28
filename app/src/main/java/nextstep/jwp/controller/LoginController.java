package nextstep.jwp.controller;


import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.http.HttpRequest;
import nextstep.jwp.model.http.HttpResponse;

import java.io.IOException;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        if (InMemoryUserRepository.existUserByAccountAndPassword(
                request.getParameter("account"),
                request.getParameter("password"))) {
            response.redirect("/index.html");
            return;
        }

        response.redirect("/401.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.forward("/login.html");
    }
}

