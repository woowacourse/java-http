package com.techcourse.controller;

import com.techcourse.dto.LoginDto;
import com.techcourse.model.User;
import com.techcourse.service.LoginService;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.StatusCode;

public class LoginController extends AbstractController {

    private final LoginService loginService = new LoginService();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (loginService.alreadyLogin(request)) {
            response.setStatus(StatusCode.FOUND);
            response.sendRedirect(request, "/index.html");
            return;
        }
        response.setStatus(StatusCode.OK);
        response.setViewUri("/login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        LoginDto loginDto = LoginDto.from(request.getQueries());
        Optional<User> user = loginService.findLoginUser(loginDto);
        if (user.isPresent()) {
            User loginUser = user.get();
            log.info("로그인 성공! 아이디: {}", loginUser.getAccount());

            Session session = request.getSession(true);
            session.setAttribute("user", loginUser);
            response.addCookie(session);
            response.setStatus(StatusCode.FOUND);
            response.sendRedirect(request, "/index.html");
            return;
        }
        response.setStatus(StatusCode.FOUND);
        response.sendRedirect(request, "/401.html");
    }
}
