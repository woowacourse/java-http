package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String[] requestHeaderInfo = br.readLine().split(" ");
            String url = requestHeaderInfo[1].substring(1);

            if (url.isEmpty()) {
                final var responseBody = "Hello world!";
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (url.contains("login")) {
                int index = url.indexOf("?");
                String staticUrl = "static/" + url.substring(0, index) + ".html";
                System.out.println(staticUrl);
                final URI uri = Objects.requireNonNull(getClass().getClassLoader().getResource(staticUrl)).toURI();
                final Path path = Paths.get(uri);
                final var contentType = Files.probeContentType(path);
                final String responseBody = Files.readString(path);

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + contentType + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();

                String queryString = url.substring(index + 1);
                String accountQuery = queryString.split("&")[0];
                String passwordQuery = queryString.split("&")[1];
                String account = accountQuery.split("=")[1];
                String password = passwordQuery.split("=")[1];

                User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지않는 계정입니다."));

                if (user.checkPassword(password)) {
                    log.info(user.toString());
                }
                return;
            }

            if (url.contains("index.html")){
                String staticUrl = "static/" + url;
                final URI uri = Objects.requireNonNull(getClass().getClassLoader().getResource(staticUrl)).toURI();
                final Path path = Paths.get(uri);

                final var contentType = Files.probeContentType(path);
                final var responseBody = Files.readString(path);

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + contentType + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
