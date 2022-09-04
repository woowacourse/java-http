package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.function.Function;
import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.ResourceNotFoundException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.Path;
import org.apache.coyote.http11.request.QueryParameters;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Controller {

    HOME("/", Controller::home),
    LOGIN("/login", Controller::login);

    private static final String WELCOME_MESSAGE = "Hello world!";
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final String path;
    private final Function<Object, ResponseEntity> function;

    Controller(String path, Function<Object, ResponseEntity> function) {
        this.path = path;
        this.function = function;
    }

    public static ResponseEntity processRequest(Request request) {
        Path path = request.getPath();
        if (path.isFileRequest()) {
            return ResponseEntity.body(path.getFileName());
        }
        QueryParameters queryParameters = request.getQueryParameters();
        return Arrays.stream(values())
                .filter(controller -> path.checkRequest(controller.path))
                .findFirst()
                .orElseThrow(ResourceNotFoundException::new)
                .function.apply(queryParameters);
    }

    private static ResponseEntity home(Object o) {
        return ResponseEntity.body(WELCOME_MESSAGE);
    }

    private static ResponseEntity login(Object object) {
        QueryParameters queryParameters = (QueryParameters) object;
        if (queryParameters.isEmpty()) {
            return ResponseEntity.body("login.html");
        }
        User user = InMemoryUserRepository.findByAccount(queryParameters.getAccount())
                .orElseThrow(UserNotFoundException::new);
        if (!user.checkPassword(queryParameters.getPassword())) {
            throw new AuthenticationException();
        }
        log.info(user.toString());
        return ResponseEntity.body("redirect:index.html").status(HttpStatus.REDIRECT);
    }
}
