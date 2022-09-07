package org.apache.coyote.http11;

import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.apache.coyote.http11.request.HttpMethod.POST;

import java.util.Arrays;
import java.util.function.Function;
import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.InvalidRequestException;
import nextstep.jwp.exception.ResourceNotFoundException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Path;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.Session;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Controller {

    HOME("/", GET, Controller::home),
    LOGIN_GET("/login", GET, Controller::loginGet),
    LOGIN_POST("/login", POST, Controller::loginPost),
    REGISTER_GET("/register", GET, Controller::registerGet),
    REGISTER_POST("/register", POST, Controller::registerPost);

    private static final String WELCOME_MESSAGE = "Hello world!";
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final String path;
    private final HttpMethod httpMethod;
    private final Function<Object, ResponseEntity> function;

    Controller(String path, HttpMethod httpMethod, Function<Object, ResponseEntity> function) {
        this.path = path;
        this.httpMethod = httpMethod;
        this.function = function;
    }

    public static ResponseEntity processRequest(HttpRequest httpRequest) {
        Path path = httpRequest.getPath();
        if (path.isFileRequest()) {
            return ResponseEntity.body(path.getFileName());
        }

        return Arrays.stream(values())
                .filter(api -> mapApi(api, httpRequest))
                .findFirst()
                .orElseThrow(ResourceNotFoundException::new)
                .function.apply(httpRequest);
    }

    private static boolean mapApi(Controller api, HttpRequest httpRequest) {
        return api.httpMethod.equals(httpRequest.getHttpMethod()) && httpRequest.checkRequestPath(api.path);
    }

    private static ResponseEntity home(Object o) {
        return ResponseEntity.body(WELCOME_MESSAGE);
    }

    private static ResponseEntity loginGet(Object object) {
        HttpRequest httpRequest = (HttpRequest) object;
        Session session = httpRequest.getSession();
        if (session == null) {
            return ResponseEntity.body("login.html");
        }
        if (session.getAttribute("user") != null) {
            return ResponseEntity.body("redirect:index.html").status(HttpStatus.REDIRECT);
        }
        return ResponseEntity.body("login.html");
    }

    private static ResponseEntity loginPost(Object o) {
        HttpRequest httpRequest = (HttpRequest) o;
        try {
            validateUser(httpRequest);
        } catch (UserNotFoundException | AuthenticationException | InvalidRequestException e) {
            return ResponseEntity.body("redirect:401.html").status(HttpStatus.REDIRECT);
        }
        return ResponseEntity.body("redirect:index.html").status(HttpStatus.REDIRECT);
    }

    private static void validateUser(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();
        if (requestBody.isEmpty()) {
            throw new InvalidRequestException();
        }
        User user = InMemoryUserRepository.findByAccount(requestBody.get("account"))
                .orElseThrow(UserNotFoundException::new);
        if (!user.checkPassword(requestBody.get("password"))) {
            throw new AuthenticationException();
        }
        Session session = httpRequest.getSession();
        session.setAttribute("user", user);
        log.info(String.format("로그인 성공! 아이디: %s", user.getAccount()));
    }

    private static ResponseEntity registerGet(Object o) {
        return ResponseEntity.body("register.html");
    }

    private static ResponseEntity registerPost(Object o) {
        HttpRequest httpRequest = (HttpRequest) o;
        RequestBody requestBody = httpRequest.getRequestBody();
        saveUser(requestBody);
        return ResponseEntity.body("redirect:index.html").status(HttpStatus.REDIRECT);
    }

    private static void saveUser(RequestBody requestBody) {
        String account = requestBody.get("account");
        String email = requestBody.get("email");
        String password = requestBody.get("password");
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
