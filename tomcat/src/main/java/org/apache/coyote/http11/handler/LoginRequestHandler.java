package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import java.util.Objects;
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

        if (!isValidateData(account, password)) {
            return Response.badRequest();
        }

        final boolean canLogin = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();

        if (canLogin) {
            return Response.redirect("/index.html");
        }

        return Response.redirect("/401.html");
    }

    public boolean isValidateData(String account, String password) {
        return !Objects.isNull(account) && !Objects.isNull(password);
    }

    @Override
    public boolean canHandle(Request request) {
        return request.getRequestPoint().getPath().equals("/login");
    }
}
