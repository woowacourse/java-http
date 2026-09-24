package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_RESOURCE_FOLDER = "static";

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

            final String requestLine = readLine(inputStream);
            if (requestLine == null) return;

            final String response = handleRequest(requestLine, inputStream);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readLine(final InputStream inputStream) throws IOException {
        try (final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int current;
            while ((current = inputStream.read()) != -1) {
                if (current == '\r') {
                    final int next = inputStream.read();

                    if (next == '\n') {
                        break;
                    }

                    buffer.write(current);

                    if (next != -1) {
                        buffer.write(next);
                    }

                    continue;
                }

                buffer.write(current);
            }

            return buffer.toString();
        }
    }

    private String handleRequest(final String requestLine, final InputStream inputStream) throws IOException {
        final String[] parsedRequestLine = requestLine.split("\\s+");

        final String httpMethod = parsedRequestLine[0];
        final String requestTarget = parsedRequestLine[1];

        final int contentLength = getContentLength(inputStream);
        final String messageBody = readMessageBody(contentLength, inputStream);

        if (httpMethod.equals("GET")) {
            return handleGetRequest(requestTarget);
        }

        if (httpMethod.equals("POST")) {
            return handlePostRequest(requestTarget, messageBody);
        }

        return createResponse(new ForwardResponse(HttpStatusCode.NOT_FOUND, DEFAULT_RESOURCE_FOLDER + "/404.html"));
    }

    private int getContentLength(final InputStream reader) throws IOException {
        final Map<String, String> messageHeaders = readMessageHeaders(reader);
        return Integer.parseInt(messageHeaders.getOrDefault("Content-Length", "0"));
    }

    private Map<String, String> readMessageHeaders(final InputStream reader) throws IOException {
        final Map<String, String> messageHeaders = new HashMap<>();
        String line;
        while  (!(line = readLine(reader)).isBlank()) {
            final String[] parsedHeader = line.split(":\\s+");
            messageHeaders.put(parsedHeader[0], parsedHeader[1].trim());
        }
        return messageHeaders;
    }

    private String readMessageBody(final int contentLength, final InputStream inputStream) throws IOException {
        if (contentLength == 0) {
            return null;
        }
        final byte[] messageBody = inputStream.readNBytes(contentLength);
        return new String(messageBody);
    }

    private String createResponse(final ForwardResponse redirectResponse) throws IOException {
        final HttpStatusCode httpStatusCode = redirectResponse.httpStatusCode();
        final String contentType = URLConnection.guessContentTypeFromName(redirectResponse.resourcePath());
        final var responseBody = readResource(redirectResponse.resourcePath());

        return String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode.getStatusCode() + " " + httpStatusCode.getReasonPhrase() + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createResponse(final RedirectResponse redirectResponse) {
        final HttpStatusCode httpStatusCode = redirectResponse.httpStatusCode();
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode.getStatusCode() + " " + httpStatusCode.getReasonPhrase() + " ",
                "Location: " + redirectResponse.redirectURL() + " ",
                "Content-Length: 0 ",
                "",
                ""
        );
    }

    private String handleGetRequest(final String requestTarget) throws IOException {
        if (requestTarget.equals("/")) {
            return createResponse(new ForwardResponse(HttpStatusCode.OK, DEFAULT_RESOURCE_FOLDER + "/index.html"));
        }

        if (requestTarget.equals("/login")) {
            return createResponse(new ForwardResponse(HttpStatusCode.OK, DEFAULT_RESOURCE_FOLDER + "/login.html"));
        }

        if (requestTarget.equals("/register")) {
            return createResponse(new ForwardResponse(HttpStatusCode.OK, DEFAULT_RESOURCE_FOLDER + "/register.html"));
        }

        return createResponse(new ForwardResponse(HttpStatusCode.OK, DEFAULT_RESOURCE_FOLDER + requestTarget));
    }

    private String handlePostRequest(final String requestTarget, final String messageBody) throws IOException {
        if (requestTarget.equals("/login")) {
            final boolean hasLoginSucceeded = loginAndRetrieveUserInfo(messageBody);
            if (hasLoginSucceeded) {
                return createResponse(new RedirectResponse(HttpStatusCode.FOUND, "/index.html"));
            }
            return createResponse(new RedirectResponse(HttpStatusCode.FOUND, "/401.html"));
        }

        if (requestTarget.equals("/register")) {
            final boolean isRegistered  = registerNewUser(messageBody);
            if (isRegistered) {
                return createResponse(new RedirectResponse(HttpStatusCode.FOUND, "/index.html"));
            }
            return createResponse(new ForwardResponse(HttpStatusCode.BAD_REQUEST, DEFAULT_RESOURCE_FOLDER + "/register.html"));
        }

        return createResponse(new ForwardResponse(HttpStatusCode.NOT_FOUND, DEFAULT_RESOURCE_FOLDER + "/404.html"));
    }

    private boolean loginAndRetrieveUserInfo(final String requestURI) {
        final int index = requestURI.indexOf("?");
        final Map<String, String> loginInfoPairs = parseQuery(requestURI.substring(index + 1));
        String account = loginInfoPairs.getOrDefault("account", "");
        String password = loginInfoPairs.getOrDefault("password", "");

        if (!account.isBlank() && !password.isBlank()) {
            Optional<User> retrieveResult = InMemoryUserRepository.findByAccount(account);
            if (retrieveResult.isEmpty()) {
                return false;
            }
            final User retrievedUser = retrieveResult.get();
            if (retrievedUser.checkPassword(password)) {
                log.info("로그인 성공! 아이디 : {}", retrievedUser.getAccount());
                return true;
            }
        }

        return false;
    }

    private Map<String, String> parseQuery(final String queryString)  {
        final Map<String, String> queryPairs = new HashMap<>();
        for (String queryPair : queryString.split("&")) {
            final int splitIndex = queryPair.indexOf("=");
            final String key = queryPair.substring(0, splitIndex).trim();
            final String value = queryPair.substring(splitIndex + 1).trim();
            queryPairs.put(key, value);
        }
        return queryPairs;
    }

    private boolean registerNewUser(final String messageBody) {
        final Map<String, String> registerInfoPairs = parseQuery(messageBody);
        String account = registerInfoPairs.getOrDefault("account", "");
        String password = registerInfoPairs.getOrDefault("password", "");
        String email = registerInfoPairs.getOrDefault("email", "");

        if (!account.isBlank() && !password.isBlank() && !email.isBlank()) {
            final User newUser = new User(account, password, email);
            InMemoryUserRepository.save(newUser);
            return true;
        }

        return false;
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
}
