package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.service.UserService;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private final Logger log;
    private final Socket connection;
    private final UserService userService;

    public Http11Processor(final Socket connection) {
        this.log = LoggerFactory.getLogger(getClass());
        this.connection = connection;
        this.userService = new UserService();
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final String response = getResponse(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(final HttpRequest httpRequest) {
        final String requestUri = httpRequest.getUri();

        if (requestUri.contains("/login")) {
            return createLoginResponse(httpRequest);
        }
        if (!requestUri.equals("/")) {
            return createResponse(StatusCode.OK, createResponseBody(requestUri), ContentType.findByUri(requestUri));
        }

        return createResponse(StatusCode.OK, "Hello world!", ContentType.HTML);
    }

    private String createLoginResponse(final HttpRequest httpRequest) {
        if (httpRequest.containsQuery()) {
            userService.login(httpRequest);
            return createResponse(StatusCode.FOUND, createResponseBody("/index.html"), ContentType.HTML);
        }
        return createResponse(StatusCode.OK, createResponseBody("/login.html"), ContentType.HTML);
    }

    private String createResponseBody(final String pathUri) {
        final URL url = getClass().getClassLoader().getResource("static" + pathUri);
        Objects.requireNonNull(url);

        try {
            final File file = new File(url.getPath());
            final Path path = file.toPath();
            return new String(Files.readAllBytes(path));
        } catch (final IOException e) {
            log.error("invalid resource", e);
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }
    }

    private String createResponse(final StatusCode statusCode, final String responseBody,
                                  final ContentType contentType) {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode.toString() + " ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
