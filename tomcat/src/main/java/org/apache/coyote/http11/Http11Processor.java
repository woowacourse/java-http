package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.common.ContentType;
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
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest httpRequest = HttpRequest.from(bufferedReader);

            String responseBody;
            String contentType;

            if (httpRequest.getUri().getValue().equals("/")) {
                responseBody = "Hello world!";
                contentType = ContentType.TEXT_HTML.getType();
            } else if (httpRequest.getUri().getValue().startsWith("/login")) {
                Map<String, String> queryParams = httpRequest.getUri().getQueryParameters();
                User user = InMemoryUserRepository.findByAccount(queryParams.get("account"))
                    .orElseThrow(NotFoundUserException::new);
                validCheckPassword(queryParams, user);

                URL resource = getClass().getClassLoader().getResource("static" + "/login.html");
                String file = Objects.requireNonNull(resource).getFile();
                responseBody = new String(Files.readAllBytes(new File(file).toPath()));
                contentType = ContentType.TEXT_HTML.getType();
            } else {
                URL resource = getClass().getClassLoader().getResource("static" + httpRequest.getUri().getValue());
                String file = Objects.requireNonNull(resource).getFile();
                responseBody = new String(Files.readAllBytes(new File(file).toPath()));
                contentType = ContentType.from(httpRequest.getUri().getValue().split("\\.")[1]).getType();
            }

            final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void validCheckPassword(final Map<String, String> queryParams, final User user) {
        if (user.checkPassword(queryParams.get("password"))) {
            log.info("user : {}", user);
            return;
        }
        throw new NotFoundUserException();
    }
}
