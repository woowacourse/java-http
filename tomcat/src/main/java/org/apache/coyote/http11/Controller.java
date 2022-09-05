package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.function.Function;
import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.ResourceNotFoundException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Controller {

    HOME("/", Controller::home),
    LOGIN("/login", Controller::login);

    private static final String WELCOME_MESSAGE = "Hello world!";
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final String path;
    private final Function<Object, String> function;

    Controller(String path, Function<Object, String> function) {
        this.path = path;
        this.function = function;
    }

    public static String processRequest(String path, QueryParameters queryParameters) {
        return Arrays.stream(values())
                .filter(controller -> path.equals(controller.path))
                .findFirst()
                .orElseThrow(ResourceNotFoundException::new)
                .function.apply(queryParameters);
    }

    private static String home(Object o) {
        return WELCOME_MESSAGE;
    }

    private static String login(Object object) {
        QueryParameters queryParameters = (QueryParameters) object;
        if (!queryParameters.isEmpty()) {
            User user = InMemoryUserRepository.findByAccount(queryParameters.getAccount())
                    .orElseThrow(UserNotFoundException::new);
            if (!user.checkPassword(queryParameters.getPassword())) {
                throw new AuthenticationException();
            }
            log.info(user.toString());
        }
        return "login.html";
    }
}
