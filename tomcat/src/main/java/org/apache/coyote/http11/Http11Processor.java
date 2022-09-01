package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import javassist.NotFoundException;
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
        try (InputStream inputStream = connection.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            OutputStream outputStream = connection.getOutputStream();

            String requestMessage = parseRequest(bufferedReader);
            HttpRequest httpRequest = HttpRequest.from(requestMessage);

            inquireUser(httpRequest);

            String responseMessage = createResponseMessage(httpRequest);
            outputStream.write(responseMessage.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | NotFoundException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequest(final BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (bufferedReader.ready()) {
            stringBuilder.append(bufferedReader.readLine())
                    .append("\r\n");
        }
        return stringBuilder.toString();
    }

    private void inquireUser(final HttpRequest httpRequest) throws NotFoundException {
        if (httpRequest.getUri().equals("/login.html")) {
            String account = httpRequest.getQueryParameters().get("account");
            String password = httpRequest.getQueryParameters().get("password");
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new NotFoundException("User not found."));
            if (user.checkPassword(password)) {
                log.info("User : {}", user);
            }
        }
    }

    private String createResponseMessage(final HttpRequest httpRequest) throws IOException {
        String requestTarget = httpRequest.getUri();
        if (requestTarget.equals("/")) {
            return response("text/html", "Hello world!");
        }
        String responseBody = createResponseBody(requestTarget);
        return response(httpRequest.getContentType(), responseBody);
    }

    private String createResponseBody(final String requestTarget) throws IOException {
        URL resource = getClass()
                .getClassLoader()
                .getResource("static" + requestTarget);
        String filePath = Objects.requireNonNull(resource)
                .getFile();
        Path path = new File(filePath).toPath();
        return new String(Files.readAllBytes(path));
    }

    private String response(final String contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
