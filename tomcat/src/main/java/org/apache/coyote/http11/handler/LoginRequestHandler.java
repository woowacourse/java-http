package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;
import org.apache.coyote.http11.data.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public Response handle(Request request) {
        final Session session = request.getSession();

        if (isLogin(session)) {
            return Response.redirect("/index.html");
        }

        final Map<String, String> body = request.getBody();
        final String account = body.get("account");
        final String password = body.get("password");

        if (!isValidateData(account, password)) {
            return Response.badRequest();
        }

        final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (optionalUser.isPresent()) {
            session.setAttribute("user", optionalUser.get());
            return Response.redirect("/index.html");
        }

        return Response.redirect("/401.html");
    }

    private boolean isLogin(Session session) {
        return session.getAttribute("user").isPresent();
    }

    private boolean isValidateData(String account, String password) {
        return !Objects.isNull(account) && !Objects.isNull(password);
    }

    @Override
    public boolean canHandle(Request request) {
        return request.getRequestPoint().getPath().equals("/login");
    }
}
