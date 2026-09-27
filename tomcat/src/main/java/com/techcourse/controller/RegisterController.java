package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class RegisterController extends AbstractController {

    @Override
    protected void get(HttpRequest request, HttpResponse response) {
        new StaticResourceController().render(request.getPath(), response);
    }

    @Override
    protected void post(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            response.setStatus(HttpStatus.CONFLICT);
            response.setHeader("Content-Type", "text/html;charset=utf-8");
            response.setBody("이미 존재하는 아이디입니다.");
            return;
        }

        User user = new User(account, request.getParameter("password"), request.getParameter("email"));
        InMemoryUserRepository.save(user);
        response.setStatus(HttpStatus.FOUND);
        response.setHeader("Location", "/index.html");
    }
}
