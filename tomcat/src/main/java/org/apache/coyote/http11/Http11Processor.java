package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final String STATIC_RESOURCE_PREFIX = "static";
    private static final String ROOT_RESPONSE_BODY = "Hello world!";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_RESOURCE_PATH = "/login.html";
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }
            String[] parts = requestLine.split(" ");

            RequestTarget requestTarget = new RequestTarget(parts[1]);
            if (requestTarget.hasPath(LOGIN_PATH)) {
                Optional<String> account = requestTarget.queryParameter("account");
                if (account.isPresent()) {
                    Optional<User> user = InMemoryUserRepository.findByAccount(account.get());
                    user.ifPresent(value -> log.info("user : {}", value));
                }
            }

            String resourcePath = resolveResourcePath(requestTarget);

            byte[] responseBody = ROOT_RESPONSE_BODY.getBytes();
            if (!resourcePath.equals("/")) {
                String fileName = STATIC_RESOURCE_PREFIX + resourcePath;
                URL resource = getClass().getClassLoader().getResource(fileName);
                if (resource != null) {
                    Path path = Paths.get(resource.toURI());
                    responseBody = Files.readAllBytes(path);
                }
            }
            String contentType = contentTypeOf(requestTarget.getExtension());

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.length + " ",
                    "") + "\r\n";

            outputStream.write(response.getBytes());
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String resolveResourcePath(RequestTarget requestTarget) {
        if (requestTarget.hasPath(LOGIN_PATH)) {
            return LOGIN_RESOURCE_PATH;
        }
        return requestTarget.getPath();
    }

    private String contentTypeOf(String extension) {
        if (extension.equals("css")) {
            return "text/css;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }
}
