package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.forward("/register.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        if (request.getParamSize() < 3) {
            log.debug("회원가입 파라미터가 부족합니다.");
            response.forward("/register.html");
            return;
        }
        User user = new User(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        );
        InMemoryUserRepository.save(user);
        log.debug("User : {}", user);
        response.sendRedirect("/index.html");
    }
}
