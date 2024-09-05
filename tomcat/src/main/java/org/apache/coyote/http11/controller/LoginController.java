package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.coyote.http11.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public boolean canHandle(String url) {
        return url.contains("login");
    }

    @Override
    public String handle(RequestLine requestLine) {
        if (requestLine.isQueryStringRequest()) {
            return checkLogin(requestLine);
        }

        return "/login.html";
    }

    private String checkLogin(RequestLine requestLine) {
        Map<String, String> parameters = requestLine.getParameters();
        String account = parameters.get("account");
        String password = parameters.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);

        if (!user.checkPassword(password)) {
            throw new SecurityException("잘못된 유저 정보 입니다.");
        }

        log.info("user : {}", user);
        return "/index.html";
    }
}
