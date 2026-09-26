package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public final class RegisterController extends AbstractController {

    private final StaticResourceController page =
            new StaticResourceController("static/register.html", "text/html;charset=utf-8");

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        page.render(response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        final String account = request.parameter("account");
        final String password = request.parameter("password");
        final String email = request.parameter("email");
        if (account == null || password == null || email == null) {
            throw new IOException("회원가입 필수 항목이 누락되었습니다.");
        }
        if (!InMemoryUserRepository.save(new User(account, password, email))) {
            response.status(409, "Conflict");
            response.header("Content-Type", "text/plain;charset=utf-8");
            response.body("이미 사용 중인 계정입니다.".getBytes(StandardCharsets.UTF_8));
            return;
        }
        response.redirect("/index.html");
    }
}
