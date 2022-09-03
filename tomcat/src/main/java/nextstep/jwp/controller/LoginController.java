package nextstep.jwp.controller;

import static nextstep.jwp.handler.StaticHandler.handleStatic;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIED_PAGE = "/401.html";

    public static String handle(HttpRequest request) throws IOException {
        QueryString queryString = request.queryString();
        if (queryString.isEmpty()) {
            return handleStatic(LOGIN_PAGE);
        }
        String account = queryString.get("account");
        String password = queryString.get("password");

        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (isInvalidUser(password, userOptional)) {
            return handleStatic(UNAUTHORIED_PAGE);
        }
        User user = userOptional.get();
        log.info(user.toString());
        return handleStatic(LOGIN_PAGE);
    }

    private static boolean isInvalidUser(String password, Optional<User> userOptional) {
        return userOptional.isEmpty() ||
                !userOptional.get()
                        .checkPassword(password);
    }
}
