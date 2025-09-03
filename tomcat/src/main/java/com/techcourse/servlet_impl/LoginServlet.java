package com.techcourse.servlet_impl;

import com.java.servlet.HttpRequest;
import com.java.servlet.HttpResponse;
import com.java.servlet.Servlet;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.java.servlet.HttpRequest.HttpMethod.GET;

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
            throw new IllegalArgumentException();
        }

        User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException();
        }

        log.info("로그인 성공, user={}", user);
        return HttpResponse.ok().html("로그인 성공!").build();
    }
}
