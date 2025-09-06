package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public HttpResponse login(HttpRequest httpRequest) {
        String account = httpRequest.getQueryStringOf("account");
        String password = httpRequest.getQueryStringOf("password");
        if (account == null && password == null) {
            return buildLoginHtmlResponse();
        }
        if (account != null && password != null) {
            User user = findUserWithAccountAndPassword(account, password);
            logger.info(user.toString());
            return buildLoginHtmlResponse();
        }
        return new HttpResponse("400 Bad Request", "text/html;charset=utf-8", "잘못된 요청입니다.");

    }

    private HttpResponse buildLoginHtmlResponse() {
        try {
            URL resourceUrl = getClass().getClassLoader().getResource("static/login.html");
            String responseBody = Files.readString(Path.of(resourceUrl.toURI()));
            return new HttpResponse("200 OK", "text/html;charset=utf-8", responseBody);
        } catch (IOException | URISyntaxException exception) {
            logger.error(exception.getMessage(), exception);
            return new HttpResponse("404 Not Found", "text/html;charset=utf-8", null);
        }
    }

    private User findUserWithAccountAndPassword(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
            .orElse(null);
        validateAccountAndPassword(user, password);
        return user;
    }

    private void validateAccountAndPassword(User user, String password) {
        if (user == null) {
            throw new IllegalArgumentException("잘못된 아이디 혹은 비밀번호입니다.");
        }
        if (!user.isPasswordValid(password)) {
            throw new IllegalArgumentException("잘못된 아이디 혹은 비밀번호입니다.");
        }
    }
}
