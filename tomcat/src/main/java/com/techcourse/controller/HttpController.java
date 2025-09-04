package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpController {

    private static final Logger log = LoggerFactory.getLogger(HttpController.class);

    public HttpResponse helloWorld() {
        return HttpResponse.ok("Hello world!");
    }

    public HttpResponse getIndex() {
        return HttpResponse.ok("index.html");
    }

    public HttpResponse getCssStyles() {
        return HttpResponse.ok("css/styles.css");
    }

    public HttpResponse getScripts() {
        return HttpResponse.ok("js/scripts.js");
    }


    public HttpResponse login(final String account, final String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다: %s".formatted(account)));

        if (user.checkPassword(password)) {
            log.info("user : {}", user);
        }

        return HttpResponse.ok("login.html");
    }
}
