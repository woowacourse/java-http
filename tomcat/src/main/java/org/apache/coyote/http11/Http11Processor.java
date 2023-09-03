package org.apache.coyote.http11;

import static kokodak.HttpStatusCode.FOUND;
import static kokodak.HttpStatusCode.INTERNAL_SERVER_ERROR;
import static kokodak.HttpStatusCode.UNAUTHORIZED;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import kokodak.HttpRequestMessage;
import kokodak.HttpResponseMessage;
import kokodak.StringReader;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final List<String> primitiveRequest = StringReader.readAll(bufferedReader);
            final HttpRequestMessage httpRequestMessage = new HttpRequestMessage(primitiveRequest);

            final URL defaultResourceUrl = getClass().getClassLoader().getResource("static/500.html");
            final String defaultResponseBody = new String(Files.readAllBytes(new File(defaultResourceUrl.getPath()).toPath()));
            HttpResponseMessage httpResponseMessage = HttpResponseMessage.builder()
                                                                         .httpStatusCode(INTERNAL_SERVER_ERROR)
                                                                         .header("Content-Type", "text/html;charset=utf-8")
                                                                         .header("Content-Length", String.valueOf(defaultResponseBody.getBytes().length))
                                                                         .body(defaultResponseBody)
                                                                         .build();

            if (httpRequestMessage.getRequestTarget().equals("/")) {
                final String responseBody = "Hello world!";
                httpResponseMessage = HttpResponseMessage.builder()
                                                         .header("Content-Type", "text/html;charset=utf-8")
                                                         .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                                                         .body(responseBody)
                                                         .build();
            } else if (httpRequestMessage.getRequestTarget().equals("/index.html")) {
                final String fileName = "static/index.html";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                final String responseBody = new String(Files.readAllBytes(path));
                httpResponseMessage = HttpResponseMessage.builder()
                                                         .header("Content-Type", "text/html;charset=utf-8")
                                                         .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                                                         .body(responseBody)
                                                         .build();
            } else if (httpRequestMessage.getRequestTarget().equals("/css/styles.css")) {
                final String fileName = "static/css/styles.css";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                final String responseBody = new String(Files.readAllBytes(path));
                httpResponseMessage = HttpResponseMessage.builder()
                                                         .header("Content-Type", "text/css")
                                                         .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                                                         .body(responseBody)
                                                         .build();
            } else if (httpRequestMessage.getRequestTarget().equals("/js/scripts.js")) {
                final String fileName = "static/js/scripts.js";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                final String responseBody = new String(Files.readAllBytes(path));
                httpResponseMessage = HttpResponseMessage.builder()
                                                         .header("Content-Type", "text/javascript")
                                                         .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                                                         .body(responseBody)
                                                         .build();
            } else if (httpRequestMessage.getRequestTarget().equals("/assets/chart-area.js")) {
                final String fileName = "static/assets/chart-area.js";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                final String responseBody = new String(Files.readAllBytes(path));
                httpResponseMessage = HttpResponseMessage.builder()
                                                         .header("Content-Type", "text/javascript")
                                                         .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                                                         .body(responseBody)
                                                         .build();
            } else if (httpRequestMessage.getRequestTarget().equals("/assets/chart-bar.js")) {
                final String fileName = "static/assets/chart-bar.js";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                final String responseBody = new String(Files.readAllBytes(path));
                httpResponseMessage = HttpResponseMessage.builder()
                                                         .header("Content-Type", "text/javascript")
                                                         .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                                                         .body(responseBody)
                                                         .build();
            } else if (httpRequestMessage.getRequestTarget().equals("/assets/chart-pie.js")) {
                final String fileName = "static/assets/chart-pie.js";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                final String responseBody = new String(Files.readAllBytes(path));
                httpResponseMessage = HttpResponseMessage.builder()
                                                         .header("Content-Type", "text/javascript")
                                                         .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                                                         .body(responseBody)
                                                         .build();
            } else if (httpRequestMessage.getRequestTarget().startsWith("/login")) {
                if (httpRequestMessage.hasQueryString()) {
                    final Map<String, String> queryString = httpRequestMessage.getQueryString();
                    final String account = queryString.get("account");
                    final String password = queryString.get("password");
                    final User user = InMemoryUserRepository.findByAccount(account)
                                                            .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
                    if (user.checkPassword(password)) {
                        httpResponseMessage = HttpResponseMessage.builder()
                                                                 .httpStatusCode(FOUND)
                                                                 .header("Location", "http://localhost:8080/index.html")
                                                                 .header("Content-Type", "text/html;charset=utf-8")
                                                                 .build();
                    } else {
                        httpResponseMessage = HttpResponseMessage.builder()
                                                                 .httpStatusCode(FOUND)
                                                                 .header("Location", "http://localhost:8080/401.html")
                                                                 .header("Content-Type", "text/html;charset=utf-8")
                                                                 .build();
                    }
                } else {
                    final String fileName = "static/login.html";
                    final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                    final Path path = new File(resourceUrl.getPath()).toPath();
                    final String responseBody = new String(Files.readAllBytes(path));
                    httpResponseMessage = HttpResponseMessage.builder()
                                                             .header("Content-Type", "text/html;charset=utf-8")
                                                             .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                                                             .body(responseBody)
                                                             .build();
                }
            } else if (httpRequestMessage.getRequestTarget().equals("/401.html")) {
                final String fileName = "static/401.html";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                final String responseBody = new String(Files.readAllBytes(path));
                httpResponseMessage = HttpResponseMessage.builder()
                                                         .httpStatusCode(UNAUTHORIZED)
                                                         .header("Content-Type", "text/html;charset=utf-8")
                                                         .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                                                         .body(responseBody)
                                                         .build();
            }
            final String response = httpResponseMessage.generate();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
