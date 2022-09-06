package nextstep.jwp.controller;

import static nextstep.jwp.handler.StaticHandler.handleStatic;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpRequest.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    public static String handle(HttpRequest request) throws IOException {
        QueryString queryString = request.queryString();
        if (queryString.isEmpty()) {
            return handleStatic(LOGIN_PAGE);
        }
        if (isValidUser(queryString.get("account"), queryString.get("password"))) {
            return handleStatic(LOGIN_PAGE);
        }
        return handleStatic(UNAUTHORIZED_PAGE);
    }

    private static Boolean isValidUser(String account, String password) {
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isPresent() &&
                userOptional.get()
                        .checkPassword(password)) {
            User user = userOptional.get();
            log.info(user.toString());
            return true;
        }
        return false;
    }
}
