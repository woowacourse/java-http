package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
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
             final var inputStreamReader = new InputStreamReader(inputStream,StandardCharsets.UTF_8);
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            final var firstLine = bufferedReader.readLine();
            var responseBody = "Hello world!";
            var contentType = "text/html;charset=utf-8";

            if (firstLine.contains("/index.html")) {
                final var resource = getClass().getClassLoader().getResource("static/index.html");
                if (resource != null) {
                    final var path = Paths.get(resource.toURI());
                    responseBody = Files.readString(path);
                }
            }

            if (firstLine.contains("/css/styles.css")) {
                final var resource = getClass().getClassLoader().getResource("static/css/styles.css");
                if (resource != null) {
                    final var path = Paths.get(resource.toURI());
                    responseBody = Files.readString(path);
                    contentType = "text/css;charset=utf-8";
                }
            }

            if (firstLine.contains("/js/scripts.js")) {
                final var resource = getClass().getClassLoader().getResource("static/js/scripts.js");
                if (resource != null) {
                    final var path = Paths.get(resource.toURI());
                    responseBody = Files.readString(path);
                    contentType = "text/javascript;charset=utf-8";
                }
            }

            if (firstLine.contains("/assets/chart-area.js")) {
                final var resource = getClass().getClassLoader().getResource("static/assets/chart-area.js");
                if (resource != null) {
                    final var path = Paths.get(resource.toURI());
                    responseBody = Files.readString(path);
                    contentType = "text/javascript;charset=utf-8";
                }
            }

            if (firstLine.contains("/assets/chart-bar.js")) {
                final var resource = getClass().getClassLoader().getResource("static/assets/chart-bar.js");
                if (resource != null) {
                    final var path = Paths.get(resource.toURI());
                    responseBody = Files.readString(path);
                    contentType = "text/javascript;charset=utf-8";
                }
            }

            if (firstLine.contains("/assets/chart-pie.js")) {
                final var resource = getClass().getClassLoader().getResource("static/assets/chart-pie.js");
                if (resource != null) {
                    final var path = Paths.get(resource.toURI());
                    responseBody = Files.readString(path);
                    contentType = "text/javascript;charset=utf-8";
                }
            }

            if (firstLine.contains("/login")) {
                final var resource = getClass().getClassLoader().getResource("static/login.html");
                if (resource != null) {
                    final var path = Paths.get(resource.toURI());
                    responseBody = Files.readString(path);
                    contentType = "text/html;charset=utf-8";
                }

                String[] httpHeaderFirstLine = firstLine.split(" ");
                String uri = httpHeaderFirstLine[1];
                String queryString = uri.split("\\?")[1];
                String account = queryString.split("&")[0].split("=")[1];
                String password = queryString.split("&")[1].split("=")[1];
                Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);
                if (byAccount.isPresent()) {
                    User user = byAccount.get();
                    if (user.checkPassword(password)) {
                        System.out.println("user: " + user);
                    }
                }
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
