package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.common.Request;
import org.apache.coyote.common.Response;
import org.apache.coyote.common.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public Response handle(Request request) {
        if (isInvalidParameters(request)) {
            return StaticResourceHandler.getInstance().handle(request);
        }
        try {
            login(request);
        } catch (IllegalArgumentException e) {
            return StaticResourceHandler.getInstance().handle(request, StatusCode.UNAUTHORIZED);
        }
        return new Response(
                StatusCode.FOUND,
                Map.of("Location", "/index.html",
                       "Set-Cookie", "JSESSIONID=" + UUID.randomUUID().toString()),
                null);
    }

    private boolean isInvalidParameters(Request request) {
        return !request.getParameters().containsKey("account") || !request.getParameters().containsKey("password");
    }

    private void login(Request request) {
        String account = request.getParameters().get("account");
        String password = request.getParameters().get("password");
        User findUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new IllegalArgumentException("로그인 실패"));
        log.info("user: {}", findUser);
    }
}
