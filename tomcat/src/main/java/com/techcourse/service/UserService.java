package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.List;
import java.util.UUID;
import org.apache.coyote.http11.general.HttpHeader;
import org.apache.coyote.http11.handler.controllerResponse.ControllerResponse;
import org.apache.coyote.http11.handler.controllerResponse.StaticFileResponse;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public ControllerResponse loginPage(HttpRequest httpRequest) {
        return new StaticFileResponse(HttpStatus.OK, "login");
    }

    public ControllerResponse login(HttpRequest httpRequest) {
        String account = httpRequest.getBodyValueOf("account");
        String password = httpRequest.getBodyValueOf("password");
        if (account != null && password != null) {
            return handleLoginResult(account, password);
        }
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    private ControllerResponse handleLoginResult(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
            .orElse(null);
        if (user == null || !user.isPasswordValid(password)) {
            return new StaticFileResponse(HttpStatus.UNAUTHORIZED, "401");
        }
        logger.info(user.toString());
        List<HttpHeader> headers = List.of(new HttpHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID()));
        return new StaticFileResponse(HttpStatus.OK, headers, "index");
    }

    public ControllerResponse registerPage(HttpRequest httpRequest) {
        return new StaticFileResponse(HttpStatus.OK, "register");
    }

    public ControllerResponse register(HttpRequest httpRequest) {
        String account = httpRequest.getBodyValueOf("account");
        String email = httpRequest.getBodyValueOf("email");
        String password = httpRequest.getBodyValueOf("password");
        User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
        return new StaticFileResponse(HttpStatus.CREATED, "index");
    }
}
