package org.apache.coyote.http11.responseGenerator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.utils.PathFinder;

public class LoginGetResponseMaker implements ResponseMaker {

    @Override
    public String createResponse(final HttpRequest httpRequest)
            throws URISyntaxException, IOException {
        final Path path = PathFinder.findPath("/login.html");
        final var responseBody = new String(Files.readAllBytes(path));
        final Optional<User> loginUser = httpRequest.findUserByJSessionId();
        if (loginUser.isPresent()) {
            return redirectByAlreadyLogin(responseBody);
        }
        final HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, responseBody, ContentType.HTML);
        return httpResponse.toString();
    }

    private String redirectByAlreadyLogin(final String responseBody) {
        final HttpResponse httpResponse =
                new HttpResponse(HttpStatus.FOUND, responseBody, ContentType.HTML, "/index.html");
        return httpResponse.toString();
    }
}
