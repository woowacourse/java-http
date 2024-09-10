package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import hoony.was.RequestHandler;
import java.util.Optional;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.hasMethod(HttpMethod.GET) && request.hasPath("/login");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (account == null || password == null) {
            return HttpResponse.builder()
                    .found("401.html")
                    .build();
        }
        logUser(account, password);
        if (logUser(account, password)) {
            return HttpResponse.builder()
                    .found("index.html")
                    .build();
        }
        return HttpResponse.builder()
                .found("401.html")
                .build();
    }

    private boolean logUser(String account, String password) {
        if (account == null) {
            return false;
        }
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);
        foundUser.filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> log.info("Verified user: {}", user),
                        () -> log.info("Invalid user")
                );
        return foundUser.isPresent();
    }
}
