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

    private final String path;

    public ResponseHandler(final String path) {
        this.path = path;
    }

    public String getResponse() throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }

        if (path.contains("/login")) {
            final QueryParam queryParam = new QueryParam(path);
            final UserRequest userRequest = queryParam.toUserRequest("account", "password");

            final Optional<User> user = InMemoryUserRepository.findByAccount(userRequest.getAccount());
            if (user.isEmpty()) {
                throw new UserNotFoundException();
            }
            log.info(user.get().toString());

            final URL resource = getClass()
                    .getClassLoader()
                    .getResource("static" + "/login.html");

            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        }

        final URL resource = getClass()
                .getClassLoader()
                .getResource("static" + path);

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
