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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

            final var response = getResponse(uri);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(final String uri) throws IOException {
        if ("/".equals(uri)) {
            final var content = "Hello world!";
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    getContentType(uri),
                    getContentLength(content),
                    "",
                    content);
        }

        final var responseBody = getResponseBody(uri);

        final URL resource = ClassLoader.getSystemClassLoader().getResource("static/" + responseBody.getContent());
        final File file = new File(URLDecoder.decode(Objects.requireNonNull(resource).getPath(), StandardCharsets.UTF_8));
        final String content = new String(Files.readAllBytes(file.toPath()));

        return String.join("\r\n",
                "HTTP/1.1 " + responseBody.getHttpStatus().getCode() + " " + responseBody.getHttpStatus().name() + " ",
                getContentType(uri),
                getContentLength(content),
                "",
                content);
    }

    private Response getResponseBody(final String uri) throws IOException {
        if ("/login".equals(uri)) {
            final URL resource = ClassLoader.getSystemClassLoader().getResource("static/login.html");
            final String file = URLDecoder.decode(Objects.requireNonNull(resource).getPath(), StandardCharsets.UTF_8);
            final String responseBody = new String(Files.readAllBytes(new File(file).toPath()));
            return new Response(HttpStatus.OK, responseBody);
        }
        if (uri.startsWith("/login?")) {
            int index = uri.indexOf("?");
            String queryString = uri.substring(index + 1);
            final Map<String, String> queries = Arrays.stream(queryString.split("&"))
                                                      .map(query -> query.split("="))
                                                      .collect(toMap(query -> query[0], query -> query[1]));
            return InMemoryUserRepository.findByAccount(queries.get("account"))
                                         .filter(user -> user.checkPassword(queries.get("password")))
                                         .map(this::loginSuccess)
                                         .orElseGet(() -> new Response(HttpStatus.UNAUTHORIZED, "/401.html"));
        }

        return new Response(HttpStatus.OK, uri);
    }

    private Response loginSuccess(final User user) {
        log.info("User{id={}, account={}, email={}, pasword={}", user.getId(), user.getAccount(), user.getEmail(), user.getPassword());
        return new Response(HttpStatus.FOUND, "/index.html");
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
