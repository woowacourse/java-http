package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.handler.controllerResponse.ControllerResponse;
import org.apache.coyote.http11.handler.controllerResponse.StaticFileResponse;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public ControllerResponse login(HttpRequest httpRequest) {
        String account = httpRequest.getQueryStringOf("account");
        String password = httpRequest.getQueryStringOf("password");
        if (account == null && password == null) {
            return new StaticFileResponse(HttpStatus.OK, "login");
        }
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
        return new StaticFileResponse(HttpStatus.OK, "index");
    }

    public ControllerResponse register(HttpRequest httpRequest) {
        if (httpRequest.isQueryStringsEmpty()) {
            return new StaticFileResponse(HttpStatus.OK, "register");
        }
        return new StaticFileResponse(HttpStatus.UNAUTHORIZED, "401");
    }
}
