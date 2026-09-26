package com.techcourse.controller;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URLEncoder;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestParams;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected String doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        return "/register.html";
    }

    @Override
    protected String doPost(final HttpRequest request, final HttpResponse response) {
        try {
            register(request.params());
            return "redirect:/index.html";
        } catch (final IllegalArgumentException e) {
            return "redirect:/register.html?error=" + URLEncoder.encode(e.getMessage(), UTF_8);
        }
    }

    private void register(final RequestParams params) {
        final String account = params.get("account");
        final String password = params.get("password");
        final String email = params.get("email");
        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException("계정 정보가 비어있다.");
        }
        if (InMemoryUserRepository.existsByAccount(account)) {
            throw new IllegalArgumentException("계정이 존재한다.");
        }
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
