package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public Response handle(Request request) {;
        final String account = request.getQueryParameters().get("account");
        final String password = request.getQueryParameters().get("password");

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> log.info("User {} logged in successfully.", account),
                        () -> log.info("Login failed for user {}.", account)
                );

        return Response.noContent();
    }

    @Override
    public boolean canHandle(Request request) {
        return request.getRequestPoint().getPath().equals("/login");
    }
}
