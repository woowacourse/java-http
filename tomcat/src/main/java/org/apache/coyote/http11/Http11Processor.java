package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            // 1. index.html 응답하기
            final String responseBody = createResponseBody(bufferedReader);

            // 2. CSS 지원하기
            final Map<String, String> requestHeader = new HashMap<>();
            String line = bufferedReader.readLine();
            while (!"".equals(line)) {
                final String[] headerField = line.split(": ");
                requestHeader.put(headerField[0], headerField[1]);
                line = bufferedReader.readLine();
            }
            final String contentType = requestHeader.getOrDefault("Accept", "text/html").split(",")[0];

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

    private String createResponseBody(final BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        final String requestUri = requestLine.split(" ")[1];

        // 3. Query String 파싱
        if (requestUri.contains("/login")) {
            return createLoginResponse(requestUri);
        }

        if (!requestUri.equals("/")) {
            return readFileFromPath(requestUri);
        }
        return "Hello world!";
    }

    private String createLoginResponse(final String requestUri) throws IOException {
        final int index = requestUri.indexOf("?");
        final String queryString = requestUri.substring(index + 1);
        final Map<String, String> params = new HashMap<>();
        final String[] paramPairs = queryString.split("&");

        for (final String paramPair : paramPairs) {
            final String[] pair = paramPair.split("=");
            if (pair.length == 2) {
                params.put(pair[0], pair[1]);
            }
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(params.get("account"));
        if (user.isPresent() && user.get().checkPassword(params.get("password"))) {
            log.info("user : {}", user.get());
        }

        return readFileFromPath("/login.html");
    }

    private String readFileFromPath(final String filePath) throws IOException {
        final Path path = new File(getClass().getClassLoader().getResource("static"  + filePath).getPath()).toPath();
        return new String(Files.readAllBytes(path));
    }
}
