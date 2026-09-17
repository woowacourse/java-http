package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final StaticResourceLoader resourceLoader = new StaticResourceLoader();

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = reader.readLine();

            if (line == null) {
                return;
            }

            var responseBody = "Hello world!";

            String[] tokens = line.split(" ");
            if (tokens.length < 2) {
                return;
            }

            RequestUri requestUri = new RequestUri(tokens[1]);

            String path = requestUri.getPath();

            if (!"/".equals(path)) {
                if ("/login".equals(path)) {
                    logMatchingUser(requestUri);
                    path = "/login.html";
                }

                try {
                    responseBody = resourceLoader.load(path);
                } catch (FileNotFoundException e) {
                    writeResponse(outputStream, "404 Not Found", "Not Found", "text/plain");
                    return;
                }
            }

            String contentType = "text/html";

            if (path.endsWith(".css")) {
                contentType = "text/css";
            }

            writeResponse(outputStream, "200 OK", responseBody, contentType);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void logMatchingUser(RequestUri requestUri) {
        String account = requestUri.getQueryParameter("account");
        String password = requestUri.getQueryParameter("password");

        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("회원 조회 성공: account={}", user.getAccount()));
    }

    private void writeResponse(OutputStream outputStream, String status, String responseBody, String contentType) throws IOException {
        byte[] responseBodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);

        var response = String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBodyBytes.length + " ",
                "",
                responseBody
        );
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
