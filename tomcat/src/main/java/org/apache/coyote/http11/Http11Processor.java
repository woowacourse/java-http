package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CONTENT_TYPE_PREFIX = "Accept: ";
    private static final String DEFAULT_RESPONSE_BODY_MESSAGE = "Hello world!";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            List<String> request = readRequest(bufferedReader);
            HttpRequest httpRequest = HttpRequest.from(request);

            String response = makeResponse(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readRequest(BufferedReader bufferedReader) throws IOException {
        List<String> request = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isBlank()) {
            request.add(line);
        }
        return request;
    }

    private String makeResponse(HttpRequest httpRequest) throws IOException {
        RequestURI requestURI = httpRequest.getRequestUrl();
        String responseBody = makeResponseBody(requestURI);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + httpRequest.contentType().getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String makeResponseBody(RequestURI requestURI) throws IOException {
        if (requestURI.isHome()) {
            return DEFAULT_RESPONSE_BODY_MESSAGE;
        }
        if (requestURI.hasQueryString()) {
            QueryString queryString = requestURI.getQueryString();
            String account = queryString.getValues().get("account");
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(IllegalArgumentException::new);
            log.info(user.toString());
        }
        Path path = new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource(requestURI.getResourcePath())).getFile()
        ).toPath();
        return new String(Files.readAllBytes(path));
    }

}
