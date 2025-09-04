package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
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
                final var outputStream = connection.getOutputStream()) {

            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final var request = bufferedReader.readLine();
            final var method = request.split(" ")[0];
            final var endpoint = request.split(" ")[1];

            if (method.equals("GET") && endpoint.equals("/")) {
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

            if (method.equals("GET") && endpoint.equals("/css/styles.css")) {
                final var path = Path.of(getClass().getResource("/static" + endpoint).getPath());
                final var responseBody = new String(Files.readAllBytes(path));

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (method.equals("GET") && endpoint.startsWith("/login")) {
                final var index = endpoint.indexOf("?");
                final var queryString = endpoint.substring(index + 1);

                final var account = queryString.split("&")[0].split("=")[1];

                final var user = InMemoryUserRepository.findByAccount(account);
                log.info("user : {}", user);

                final var urlPath = Path.of(getClass().getResource("/static" + "/login.html").getPath());

                final var responseBody = new String(Files.readAllBytes(urlPath));

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

            final var path = Path.of(getClass().getResource("/static" + endpoint).getPath());
            final var responseBody = new String(Files.readAllBytes(path));

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
