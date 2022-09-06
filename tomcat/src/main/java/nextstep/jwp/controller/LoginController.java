package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handler.StaticHandler;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryString;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    public static HttpResponse handle(HttpRequest request, HttpResponse response) throws IOException {
        QueryString queryString = request.queryString();
        if (queryString.isEmpty()) {
            return StaticHandler.handle(INDEX_PAGE, response);
        }
        if (isValidUser(queryString.get("account"), queryString.get("password"))) {
            response.setStatus(HttpStatus.FOUND);
            return StaticHandler.handle(INDEX_PAGE, response);
        }
        response.setStatus(HttpStatus.UNAUTHORIZED);
        return StaticHandler.handle(UNAUTHORIZED_PAGE, response);
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
