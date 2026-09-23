package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

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
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final String requestLine = bufferedReader.readLine();
            if (requestLine == null) return;

            final String requestTarget = requestLine.split("\\s+")[1];

            final String resourcePath = handleRequest(requestTarget);

            final var responseBody = readResource(resourcePath);
            final String contentType = requestTarget.endsWith(".css") ? "text/css" : "text/html;charset=utf-8";

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String handleRequest(final String requestTarget) {
        if (requestTarget.equals("/")) {
            return "static/index.html";
        }

        if (requestTarget.startsWith("/login")) {
            if (requestTarget.contains("?")) {
                loginAndRetrieveUserInfo(requestTarget);
            }
            return "static/login.html";
        }

        return "static" + requestTarget;
    }

    private String readResource(final String resourcePath) throws IOException {
        try {
            final URI resourceURI = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(resourcePath)).toURI();
            final Path path = Path.of(resourceURI);
            return Files.readString(path);
        } catch (NullPointerException e) {
            log.error("{} 자료가 존재하지 않습니다.", resourcePath);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    private void loginAndRetrieveUserInfo(final String requestURI) {
        final int index = requestURI.indexOf("?");
        final String[] loginInfos = requestURI.substring(index + 1).split("&");
        String account = "";
        String password = "";
        for (String loginInfo : loginInfos) {
            final String[] keyAndValue = loginInfo.split("=");
            final String key = keyAndValue[0];
            final String value = keyAndValue[1];
            if (key.equals("account")) {
                account = value;
                continue;
            }
            if (key.equals("password")) {
                password = value;
            }
        }

        if (!account.isBlank() && !password.isBlank()) {
            Optional<User> retrieveResult = InMemoryUserRepository.findByAccount(account);
            if (retrieveResult.isEmpty()) {
                return;
            }
            final User retrievedUser = retrieveResult.get();
            if (retrievedUser.checkPassword(password)) {
                log.info("user : {}", retrievedUser);
            }
        }
    }
}
