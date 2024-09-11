package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import hoony.was.RequestHandler;
import hoony.was.session.SessionCookie;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.hasMethod(HttpMethod.POST) && request.hasPath("/login");
    }

    @Override
    public String handle(HttpRequest request, HttpResponse response) {
        String content = request.getContent();
        QueryParameters queryParameters = new QueryParameters(content);
        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        logUser(account, password);
        if (logUser(account, password)) {
            SessionCookie sessionCookie = new SessionCookie(UUID.randomUUID().toString());
            response.setCookie(sessionCookie.getCookie());
            return "redirect:/index.html";
        }
        return "redirect:/401.html";
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
