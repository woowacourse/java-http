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
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
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
            return createResponse(createResponseBody(requestUri), ContentType.findByUri(requestUri));
        }

        return createResponse("Hello world!", ContentType.HTML);
    }

    private String createLoginResponse(final HttpRequest httpRequest) {
        if (httpRequest.containsQuery()) {
            findUser(httpRequest);
        }
        final String responseBody = createResponseBody("/login.html");
        return createResponse(responseBody, ContentType.HTML);
    }

    private void findUser(final HttpRequest httpRequest) {
        final String userAccount = httpRequest.getParameter("account");
        final String userPassword = httpRequest.getParameter("password");

        final User user = InMemoryUserRepository.findByAccount(userAccount)
                .filter(it -> it.checkPassword(userPassword))
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 유저입니다."));

        log.info("user : {}", user);
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

    private String createResponse(final String responseBody, final ContentType contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
