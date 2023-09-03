package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.requestLine.requestUri.QueryParameters;
import org.apache.coyote.http11.request.requestLine.requestUri.RequestUri;
import org.apache.coyote.http11.request.requestLine.requestUri.ResourcePath;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.coyote.http11.response.statusLine.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    public static final String STATIC_RESOURCE_DIRECTORY = "static";

    private final HttpRequest httpRequest;

    public Controller(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public ResponseEntity run() throws IOException {
        final RequestUri requestUri = httpRequest.getRequestLine().getRequestUri();
        final ResourcePath resourcePath = requestUri.getResourcePath();
        if (resourcePath.isRootPath()) {
            return new ResponseEntity(HttpStatus.OK, ContentType.TEXT_HTML, "Hello world!");
        }

        if (resourcePath.is("/login")) {
            return loginByUri(requestUri);
        }

        final URL resourceFileUrl = ClassLoader.getSystemResource(STATIC_RESOURCE_DIRECTORY + resourcePath.getResourcePath());

        return ResponseEntity.of(HttpStatus.OK, resourceFileUrl);
    }

    private ResponseEntity loginByUri(final RequestUri requestUri) {
        final QueryParameters queryParameters = requestUri.getQueryParameters();
        login(queryParameters);

        final URL resourceFileUrl = ClassLoader.getSystemResource(STATIC_RESOURCE_DIRECTORY + "/login.html");
        return ResponseEntity.of(HttpStatus.OK, resourceFileUrl);
    }

    private void login(final QueryParameters queryParameters) {
        final String account = queryParameters.search("account");
        final String password = queryParameters.search("password");

        final User findUser = InMemoryUserRepository.findByAccount(account)
                                                    .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        if (findUser.checkPassword(password)) {
            log.info("user: {}", findUser);
        }
    }
}
