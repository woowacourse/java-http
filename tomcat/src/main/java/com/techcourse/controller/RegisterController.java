package com.techcourse.controller;

import static com.techcourse.HttpStaus.*;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.ResponseHandler;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getParameter("account").orElse(null);
        String password = request.getParameter("password").orElse(null);
        String email = request.getParameter("email").orElse(null);

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            ResponseHandler.sendStaticFile(response, "/register.html", BAD_REQUEST);
            return;
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        ResponseHandler.redirect(response, "/login.html", OK);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        ResponseHandler.sendStaticFile(response, "/register.html", OK);
    }
}
