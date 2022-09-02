package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.response.UserRequest;

import org.apache.coyote.http11.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseHandler {

    private static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);
    private static final String RESOURCE_PATH = "static";
    private static final String LOGIN_PATH = "/login";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String DEFAULT_PATH = "/";

    private final String path;

    public ResponseHandler(final String path) {
        this.path = path;
    }

    public String getResponse() throws IOException {
        if (path.equals(DEFAULT_PATH)) {
            return "Hello world!";
        }

        if (path.contains(LOGIN_PATH)) {
            return pathHasLogin();
        }

        final URL resource = getClass()
                .getClassLoader()
                .getResource(RESOURCE_PATH + path);

        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String pathHasLogin() throws IOException {
        final QueryParam queryParam = new QueryParam(path);
        final UserRequest userRequest = queryParam.toUserRequest(ACCOUNT, PASSWORD);

        final Optional<User> user = InMemoryUserRepository.findByAccount(userRequest.getAccount());
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
        log.info(user.get().toString());

        final URL resource = getClass()
                .getClassLoader()
                .getResource(RESOURCE_PATH + LOGIN_PATH + ".html");

        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    public ContentType getContentType() {
        if (path.contains(".")) {
            final String[] splitExtension = path.split("\\.");
            return ContentType.matchMIMEType(splitExtension[splitExtension.length - 1]);
        }
        return ContentType.HTML;
    }
}
