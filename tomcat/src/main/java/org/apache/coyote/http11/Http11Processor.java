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

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            var request = bufferedReader.readLine();
            log.info("request : {}", request);

            final String[] header = request.split(" ");
            final var method = header[0];
            final var uri = header[1];
            log.info("uri : {}", uri);

            if ("GET".equals(method)) {
                final var response = getResponse(uri);

                outputStream.write(response.getBytes());
                outputStream.flush();
            }

            if ("POST".equals(method)) {
                final var responseBody = getPostResponse(bufferedReader, request, uri);
                final String response = getContent(uri, responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static Response getPostResponse(final BufferedReader bufferedReader, String request, final String uri) throws IOException {
        if ("/register".equals(uri)) {
            Integer contentLength = null;
            while (request != null && !"".equals(request)) {
                request = bufferedReader.readLine();
                if (request.contains("Content-Length")) {
                    request = request.replaceAll(" ", "");
                    contentLength = Integer.parseInt(request.split(":")[1]);
                }
            }

            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String requestBody = URLDecoder.decode(new String(buffer), StandardCharsets.UTF_8.toString());

            final Map<String, String> queries = Arrays.stream(requestBody.split("&"))
                                                      .map(query -> query.split("="))
                                                      .collect(toMap(query -> query[0], query -> query[1]));

            if (InMemoryUserRepository.isExistAccount(queries.get("account"))) {
                return new Response(HttpStatus.OK, "login.html");
            }

            InMemoryUserRepository.save(new User(queries.get("account"), queries.get("password"), queries.get("email")));
            return new Response(HttpStatus.CREATED, "index.html");
        }
        return null;
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

        return getContent(uri, responseBody);
    }

    private String getContent(final String uri, final Response responseBody) throws IOException {
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

    private Response getResponseBody(final String uri) {
        if ("/register".equals(uri)) {
            return new Response(HttpStatus.OK, "register.html");
        }
        if ("/login".equals(uri)) {
            return new Response(HttpStatus.OK, "login.html");
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
