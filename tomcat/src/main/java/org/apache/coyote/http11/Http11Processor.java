package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {

            // 1. index.html 응답하기
            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final String responseBody = createResponseBody(httpRequest);

            // 2. CSS 지원하기
            final String contentType = httpRequest.getHeaderField("Accept");

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponseBody(final HttpRequest httpRequest) throws IOException {
        final String requestUri = httpRequest.parseUriPath();
        // 3. Query String 파싱
        if (requestUri.contains("/login")) {
            return createLoginResponse(httpRequest);
        }

        if (!requestUri.equals("/")) {
            return readFileFromPath(requestUri);
        }
        return "Hello world!";
    }

    private String createLoginResponse(final HttpRequest httpRequest) throws IOException {
        final Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getParamByName("account"));
        if (user.isPresent() && user.get().checkPassword(httpRequest.getParamByName("password"))) {
            log.info("user : {}", user.get());
        }
        return readFileFromPath("/login.html");
    }

    private String readFileFromPath(final String filePath) throws IOException {
        final Path path = new File(getClass().getClassLoader().getResource("static" + filePath).getPath()).toPath();
        return new String(Files.readAllBytes(path));
    }
}
