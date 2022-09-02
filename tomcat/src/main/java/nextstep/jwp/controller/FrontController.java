package nextstep.jwp.controller;

import static org.reflections.Reflections.log;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class FrontController {

    public HttpResponse run(HttpRequest httpRequest) throws IOException {
        if (httpRequest.getRequestUri().equals("/")) {
            final String responseBody = "Hello world!";
            return new HttpResponse(httpRequest.getProtocol(), HttpStatus.OK, "text/html;charset=utf-8", responseBody);
        }
        if (httpRequest.getRequestUri().endsWith(".html")) {
            if (httpRequest.getRequestUri().equals("/login.html")) {
                Optional<User> user = InMemoryUserRepository.findByAccount(
                        httpRequest.getQueryStrings().get("account"));
                user.ifPresent(value -> log.debug(value.toString()));
            }
            final String responseBody = getResponseBody(httpRequest);
            return new HttpResponse(httpRequest.getProtocol(), HttpStatus.OK, "text/html;charset=utf-8", responseBody);
        }
        if (httpRequest.getRequestUri().endsWith(".css")) {
            final String responseBody = getResponseBody(httpRequest);
            return new HttpResponse(httpRequest.getProtocol(), HttpStatus.OK, "text/css;charset=utf-8", responseBody);

        }
        if (httpRequest.getRequestUri().endsWith(".js")) {
            final String responseBody = getResponseBody(httpRequest);
            return new HttpResponse(httpRequest.getProtocol(), HttpStatus.OK, "text/javascript;charset=utf-8",
                    responseBody);
        }
        return new HttpResponse(httpRequest.getProtocol(), HttpStatus.NOT_FOUND, "text/html;charset=utf-8", null);
    }

    private String getResponseBody(final HttpRequest httpRequest) throws IOException {
        final URL resourceURL = getClass().getClassLoader()
                .getResource("static" + httpRequest.getRequestUri());
        try {
            final File file = new File(Objects.requireNonNull(resourceURL).toURI());
            final Path path = file.getAbsoluteFile().toPath();
            return new String(Files.readAllBytes(path));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
