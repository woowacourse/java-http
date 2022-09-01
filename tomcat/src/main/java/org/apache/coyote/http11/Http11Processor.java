package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.ContentType;
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

            final String extractUri = extractUri(bufferedReader.readLine());

            String responseBody;
            String contentType;

            if (extractUri.equals("/")) {
                responseBody = "Hello world!";
                contentType = ContentType.TEXT_HTML.getType();
            } else if (extractUri.startsWith("/login?")) {
                final String queryString = extractUri.split("[?]")[1];
                final Map<String, String> queryParams = extractQueryParam(queryString);

                User user = InMemoryUserRepository.findByAccount(queryParams.get("account"))
                    .orElseThrow(NotFoundUserException::new);
                validCheckPassword(queryParams, user);

                final URL resource = getClass().getClassLoader().getResource("static" + "/login.html");
                String file = Objects.requireNonNull(resource).getFile();
                responseBody = new String(Files.readAllBytes(new File(file).toPath()));
                contentType = ContentType.TEXT_HTML.getType();
            } else {
                final URL resource = getClass().getClassLoader().getResource("static" + extractUri);
                String file = Objects.requireNonNull(resource).getFile();
                responseBody = new String(Files.readAllBytes(new File(file).toPath()));
                contentType = ContentType.from(extractUri.split("\\.")[1]).getType();
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

    private String extractUri(final String line) {
        final String[] uri = line.split(" ");
        return uri[1];
    }

    private Map<String, String> extractQueryParam(String queryString) {
        Map<String, String> params = new HashMap<>();

        final String[] parameters = queryString.split("&");
        for (String parameter : parameters) {
            final String[] splitParameter = parameter.split("=");
            params.put(splitParameter[0], splitParameter[1]);
        }
        return params;
    }

    private void validCheckPassword(final Map<String, String> queryParams, final User user) {
        if (user.checkPassword(queryParams.get("password"))) {
            log.info("user : {}", user);
            return;
        }
        throw new NotFoundUserException();
    }
}
