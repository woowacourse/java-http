package com.http.application.servlet.impl;

import com.http.application.servlet.RequestServlet;
import com.http.domain.HttpRequest;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestServlet implements RequestServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginRequestServlet.class);

    @Override
    public void handle(HttpRequest httpRequest) {
        final Map<String, String> queryStrings = httpRequest.queryStrings();

        String account = queryStrings.get("account");
        String password = queryStrings.get("password");
        processLogin(account, password);
    }

    private void processLogin(String account, String password) {
        if (account == null || password == null) {
            throw new IllegalArgumentException("account와 password는 필수입니다.");
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        if (user.checkPassword(password)) {
            log.info("user : {} ", user);
        }
    }
}
