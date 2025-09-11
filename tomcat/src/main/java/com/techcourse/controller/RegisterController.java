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
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String method = request.getMethod();
        if (method.equals("GET")) {
            doGet(request, response);
        }
        if (method.equals("POST")) {
            doPost(request, response);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getParameter("account").orElse(null);
        String password = request.getParameter("password").orElse(null);
        String email = request.getParameter("email").orElse(null);

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            ResponseHandler.sendStaticFile(response,"static/register.html", BAD_REQUEST);
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        ResponseHandler.redirect(response,"static/login.html", OK);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        ResponseHandler.sendStaticFile(response, "static/register.html", OK);
    }
}
