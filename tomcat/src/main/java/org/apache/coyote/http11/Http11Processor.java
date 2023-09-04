package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
             final var outputStream = connection.getOutputStream()) {

            final var request = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            log.info("request : {}", request);

            final var uri = request.split(" ")[1];
            log.info("uri : {}", uri);

            final var responseBody = getResponseBody(uri);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    getContentType(uri),
                    getContentLength(responseBody),
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(final String uri) throws IOException {
        if ("/".equals(uri)) {
            return "Hello world!";
        }
        if (uri.startsWith("/login?")) {
            int index = uri.indexOf("?");
            String queryString = uri.substring(index + 1);
            final Map<String, String> queries = Arrays.stream(queryString.split("&"))
                                                      .map(query -> query.split("="))
                                                      .collect(toMap(query -> query[0], query -> query[1]));
            final User userInfo = InMemoryUserRepository.findByAccount(queries.get("account"))
                                                        .filter(user -> user.checkPassword(queries.get("password")))
                                                        .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));

            log.info("User{id={}, account={}, email={}, pasword={}", userInfo.getId(), userInfo.getAccount(), userInfo.getEmail(), userInfo.getPassword());

            final URL resource = ClassLoader.getSystemClassLoader().getResource("static/login.html");
            final String file = Objects.requireNonNull(resource).getFile();
            return new String(Files.readAllBytes(new File(file).toPath()));
        }

        final URL resource = ClassLoader.getSystemClassLoader().getResource("static" + uri);
        final String file = Objects.requireNonNull(resource).getFile();
        return new String(Files.readAllBytes(new File(file).toPath()));
    }

    private String getContentType(final String uri) {
        if (uri.endsWith(".css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }

        return "Content-Type: text/html;charset=utf-8 ";
    }

    private String getContentLength(final String responseBody) {
        return "Content-Length: " + responseBody.getBytes().length + " ";
    }
}
