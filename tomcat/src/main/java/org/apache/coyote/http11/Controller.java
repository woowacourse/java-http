package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.requestLine.HttpMethod;
import org.apache.coyote.http11.request.requestLine.requestUri.QueryParameters;
import org.apache.coyote.http11.request.requestLine.requestUri.RequestUri;
import org.apache.coyote.http11.request.requestLine.requestUri.ResourcePath;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.coyote.http11.response.statusLine.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    private static final String ROOT_DIRECTORY = "/";
    private static final String DIRECTORY_SEPARATOR = "/";
    private static final String INDEX_FILE = "index.html";
    private static final String REGISTER_FILE = "register.html";

    private final HttpRequest httpRequest;

    public Controller(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public ResponseEntity run() {
        final RequestUri requestUri = httpRequest.getRequestLine().getRequestUri();
        final ResourcePath resourcePath = requestUri.getResourcePath();
        if (resourcePath.isRootPath()) {
            return new ResponseEntity(HttpStatus.OK, ContentType.TEXT_HTML, "Hello world!");
        }

        if (resourcePath.is("/login")) {
            return login(requestUri);
        }

        if (resourcePath.is("/register")) {
            return register(httpRequest);
        }

        return ResponseEntity.of(HttpStatus.OK, resourcePath.getResourcePath());
    }

    private ResponseEntity login(final RequestUri requestUri) {
        if (requestUri.isQueryParameterEmpty()) {
            return ResponseEntity.of(HttpStatus.OK, "/login.html");
        }

        final QueryParameters queryParameters = requestUri.getQueryParameters();

        final String account = queryParameters.search("account");
        final String password = queryParameters.search("password");

        return InMemoryUserRepository.findByAccount(account)
                                     .filter(user -> user.checkPassword(password))
                                     .map(this::loginSuccess)
                                     .orElseGet(this::loginFail);
    }

    private ResponseEntity loginSuccess(final User findUser) {
        log.info("user: {}", findUser);
        final String indexPath = DIRECTORY_SEPARATOR + INDEX_FILE;

        return ResponseEntity.of(HttpStatus.FOUND, indexPath);
    }

    private ResponseEntity loginFail() {
        final String unauthorizedPath = DIRECTORY_SEPARATOR + HttpStatus.UNAUTHORIZED.getResourceName();

        return ResponseEntity.of(HttpStatus.UNAUTHORIZED, unauthorizedPath);
    }

    private ResponseEntity register(final HttpRequest httpRequest) {
        if (httpRequest.isRequestOf(HttpMethod.POST)) {
            return postRegister(httpRequest.getRequestBody());
        }
        if (httpRequest.isRequestOf(HttpMethod.GET)) {
            final String registerResourcePath = DIRECTORY_SEPARATOR + REGISTER_FILE;

            return ResponseEntity.of(HttpStatus.OK, registerResourcePath);
        }

        return ResponseEntity.of(HttpStatus.BAD_REQUEST, ROOT_DIRECTORY);
    }

    private ResponseEntity postRegister(final RequestBody requestBody) {
        final String account = requestBody.search("account");
        final String password = requestBody.search("password");
        final String email = requestBody.search("email");
        final User user = new User(account, password, email);

        try {
            InMemoryUserRepository.save(user);
        } catch (IllegalArgumentException e) {
            final String registerPath = DIRECTORY_SEPARATOR + REGISTER_FILE;
            return ResponseEntity.of(HttpStatus.BAD_REQUEST, registerPath);
        }

        final String indexPath = DIRECTORY_SEPARATOR + INDEX_FILE;
        return ResponseEntity.of(HttpStatus.FOUND, indexPath);
    }
}
