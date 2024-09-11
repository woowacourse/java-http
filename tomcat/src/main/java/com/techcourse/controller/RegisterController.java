package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.sendStaticResourceResponse("/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.findBodyValueByKey("account");
        String password = request.findBodyValueByKey("password");
        String email = request.findBodyValueByKey("email");

        validateInput(account, password, email);

        InMemoryUserRepository.save(new User(account, password, email));
        response.sendRedirect("/index.html");
    }

    private void validateInput(String account, String password, String email) {
        if (account == null) {
            throw new IllegalArgumentException("아이디를 입력해주세요.");
        }
        if (password == null) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
        if (email == null) {
            throw new IllegalArgumentException("이메일을 입력해주세요");
        }
    }
}
