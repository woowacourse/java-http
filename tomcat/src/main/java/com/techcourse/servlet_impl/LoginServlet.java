package com.techcourse.servlet_impl;

import com.java.http.HttpRequest;
import com.java.http.HttpResponse;
import com.java.servlet.Servlet;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.java.http.HttpRequest.HttpMethod.GET;

public class LoginServlet implements Servlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals("/login");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String account = request.param("account");
        String password = request.param("password");
        if (account == null || password == null) {
            throw new IllegalArgumentException("계정과 비밀번호는 필수입니다.");
        }

        User user = InMemoryUserRepository.findByAccount(account).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        log.info("로그인 성공, user={}", user);
        return HttpResponse.ok().html("로그인 성공!").build();
    }
}
