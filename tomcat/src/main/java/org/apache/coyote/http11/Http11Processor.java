package org.apache.coyote.http11;

import static kokodak.Constants.CRLF;

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

            String responseBody = "";
            String response = "";

            if (httpRequestMessage.getRequestTarget().equals("/")) {
                responseBody = "Hello world!";
                response = String.join(CRLF.getValue(),
                                       "HTTP/1.1 200 OK ",
                                       "Content-Type: text/html;charset=utf-8 ",
                                       "Content-Length: " + responseBody.getBytes().length + " ",
                                       "",
                                       responseBody);
            } else if (httpRequestMessage.getRequestTarget().equals("/index.html")) {
                final String fileName = "static/index.html";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                responseBody = new String(Files.readAllBytes(path));
                response = String.join(CRLF.getValue(),
                                       "HTTP/1.1 200 OK ",
                                       "Content-Type: text/html;charset=utf-8 ",
                                       "Content-Length: " + responseBody.getBytes().length + " ",
                                       "",
                                       responseBody);
            } else if (httpRequestMessage.getRequestTarget().equals("/css/styles.css")) {
                final String fileName = "static/css/styles.css";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                responseBody = new String(Files.readAllBytes(path));
                response = String.join(CRLF.getValue(),
                                       "HTTP/1.1 200 OK ",
                                       "Content-Type: text/css ",
                                       "Content-Length: " + responseBody.getBytes().length + " ",
                                       "",
                                       responseBody);
            } else if (httpRequestMessage.getRequestTarget().equals("/js/scripts.js")) {
                final String fileName = "static/js/scripts.js";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                responseBody = new String(Files.readAllBytes(path));
                response = String.join(CRLF.getValue(),
                                       "HTTP/1.1 200 OK ",
                                       "Content-Type: text/javascript ",
                                       "Content-Length: " + responseBody.getBytes().length + " ",
                                       "",
                                       responseBody);
            } else if (httpRequestMessage.getRequestTarget().equals("/assets/chart-area.js")) {
                final String fileName = "static/assets/chart-area.js";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                responseBody = new String(Files.readAllBytes(path));
                response = String.join(CRLF.getValue(),
                                       "HTTP/1.1 200 OK ",
                                       "Content-Type: text/javascript ",
                                       "Content-Length: " + responseBody.getBytes().length + " ",
                                       "",
                                       responseBody);
            } else if (httpRequestMessage.getRequestTarget().equals("/assets/chart-bar.js")) {
                final String fileName = "static/assets/chart-bar.js";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                responseBody = new String(Files.readAllBytes(path));
                response = String.join(CRLF.getValue(),
                                       "HTTP/1.1 200 OK ",
                                       "Content-Type: text/javascript ",
                                       "Content-Length: " + responseBody.getBytes().length + " ",
                                       "",
                                       responseBody);
            } else if (httpRequestMessage.getRequestTarget().equals("/assets/chart-pie.js")) {
                final String fileName = "static/assets/chart-pie.js";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                responseBody = new String(Files.readAllBytes(path));
                response = String.join(CRLF.getValue(),
                                       "HTTP/1.1 200 OK ",
                                       "Content-Type: text/javascript ",
                                       "Content-Length: " + responseBody.getBytes().length + " ",
                                       "",
                                       responseBody);
            } else if (httpRequestMessage.getRequestTarget().startsWith("/login")) {
                if (httpRequestMessage.hasQueryString()) {
                    final Map<String, String> queryString = httpRequestMessage.getQueryString();
                    final String account = queryString.get("account");
                    final String password = queryString.get("password");
                    final User user = InMemoryUserRepository.findByAccount(account)
                                                            .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
                    if (user.checkPassword(password)) {
                        log.info("user = {}", user);
                    }
                }
                final String fileName = "static/login.html";
                final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
                final Path path = new File(resourceUrl.getPath()).toPath();
                responseBody = new String(Files.readAllBytes(path));
                response = String.join(CRLF.getValue(),
                                       "HTTP/1.1 200 OK ",
                                       "Content-Type: text/html;charset=utf-8 ",
                                       "Content-Length: " + responseBody.getBytes().length + " ",
                                       "",
                                       responseBody);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
