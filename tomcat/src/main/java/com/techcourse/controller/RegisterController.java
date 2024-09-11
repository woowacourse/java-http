package com.techcourse.controller;

import com.techcourse.controller.dto.RegisterRequest;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.util.StaticResourceManager;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final ResponseFile registerPage = StaticResourceManager.read("/register.html");

    @Override
    public String matchedPath() {
        return "/register";
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatusCode.OK)
                .setBody(registerPage);
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse response) {
        RegisterRequest registerRequest = RegisterRequest.of(httpRequest.getBody());
        String account = registerRequest.account();
        String password = registerRequest.password();
        String email = registerRequest.email();
        log.info("회원가입 요청 -  id: {}, password: {}, email: {}", account, password, email);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.sendRedirect("/index.html");
    }
}
