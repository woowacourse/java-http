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
import java.net.URLConnection;
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

            final RedirectResponse redirectResponse = handleRequest(requestTarget);

            final HttpStatusCode httpStatusCode = redirectResponse.httpStatusCode();
            final String contentType = URLConnection.guessContentTypeFromName(redirectResponse.resourcePath());
            final var responseBody = readResource(redirectResponse.resourcePath());

            final var response = String.join("\r\n",
                    "HTTP/1.1 " + httpStatusCode.getStatusCode() + " " + httpStatusCode.getReasonPhrase() + " ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RedirectResponse handleRequest(final String requestTarget) {
        if (requestTarget.equals("/")) {
            return new RedirectResponse(HttpStatusCode.OK, "static/index.html");
        }

        if (requestTarget.startsWith("/login")) {
            if (requestTarget.contains("?")) {
               final boolean hasLoginSucceeded = loginAndRetrieveUserInfo(requestTarget);
               if (hasLoginSucceeded) {
                   return new RedirectResponse(HttpStatusCode.FOUND, "static/index.html");
               }
               return new RedirectResponse(HttpStatusCode.OK, "static/401.html");
            }
            return new RedirectResponse(HttpStatusCode.OK, "static/login.html");
        }

        return new RedirectResponse(HttpStatusCode.OK, "static" + requestTarget);
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

    private boolean loginAndRetrieveUserInfo(final String requestURI) {
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
                return false;
            }
            final User retrievedUser = retrieveResult.get();
            if (retrievedUser.checkPassword(password)) {
                log.info("user : {}", retrievedUser);
                return true;
            }
        }

        return false;
    }
}
