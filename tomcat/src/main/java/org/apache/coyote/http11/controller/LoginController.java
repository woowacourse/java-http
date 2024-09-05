package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
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
    public Path handle(RequestLine requestLine) {
        checkLogin(requestLine);
        URL foundUrl = getClass().getClassLoader().getResource("static/index.html");
        try {
            return Path.of(Objects.requireNonNull(foundUrl).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkLogin(RequestLine requestLine) {
        Map<String, String> parameters = requestLine.getParameters();
        String account = parameters.get("account");
        String password = parameters.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException(account + " " + password + "잘못된 유저 요청입니다.");
        }

        log.info("user : {}", user);
    }


}
