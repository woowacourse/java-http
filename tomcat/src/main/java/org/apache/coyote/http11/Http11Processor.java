package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
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
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_RESOURCE_FOLDER = "static";

    private final Socket connection;
    private final Manager sessionManager = SessionManager.getInstance();

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

            final Map<String, String> messageHeaders = readMessageHeaders(inputStream);

            final Map<String, String> responseHeader  = new HashMap<>();

            Session session;
            final HttpCookie httpCookie = HttpCookie.from(messageHeaders.get("Cookie"));
            if (httpCookie.contains("JSESSIONID")) {
                final String sessionId = httpCookie.get("JSESSIONID");
                session = sessionManager.findSession(sessionId);
            } else {
                final String sessionId = String.valueOf(UUID.randomUUID());
                session = new Session(sessionId);
                sessionManager.add(session);
                responseHeader.put("Set-Cookie", "JSESSIONID=" + sessionId);
            }

            final int contentLength = Integer.parseInt(messageHeaders.getOrDefault("Content-Length", "0"));
            final String messageBody = readMessageBody(contentLength, inputStream);
            final String response = handleRequest(requestLine, messageBody, responseHeader, session);

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

    private String handleRequest(final String requestLine, final String messageBody, final Map<String, String> responseHeaders, final Session session) throws IOException {
        final String[] parsedRequestLine = requestLine.split("\\s+");

        final String httpMethod = parsedRequestLine[0];
        final String requestTarget = parsedRequestLine[1];

        if (httpMethod.equals("GET")) {
            return handleGetRequest(requestTarget, responseHeaders, session);
        }

        if (httpMethod.equals("POST")) {
            return handlePostRequest(requestTarget, messageBody, responseHeaders, session);
        }

        return createForwardResponse(HttpStatusCode.NOT_FOUND, DEFAULT_RESOURCE_FOLDER + "/404.html", responseHeaders);
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

    private String handleGetRequest(final String requestTarget, final Map<String, String> responseHeaders, final Session session) throws IOException {
        if (requestTarget.equals("/")) {
            return createForwardResponse(HttpStatusCode.OK, DEFAULT_RESOURCE_FOLDER + "/index.html", responseHeaders);
        }

        if (requestTarget.equals("/login")) {
            if (session.getAttribute("user") != null) {
                return createRedirectResponse("/index.html", responseHeaders);
            }
            return createForwardResponse(HttpStatusCode.OK, DEFAULT_RESOURCE_FOLDER + "/login.html", responseHeaders);
        }

        if (requestTarget.equals("/register")) {
            return createForwardResponse(HttpStatusCode.OK, DEFAULT_RESOURCE_FOLDER + "/register.html", responseHeaders);
        }

        return createForwardResponse(HttpStatusCode.OK, DEFAULT_RESOURCE_FOLDER + requestTarget, responseHeaders);
    }

    private String createForwardResponse(final HttpStatusCode httpStatusCode, final String resourcePath, final Map<String, String> responseHeaders) throws IOException {
        final String headers = parseResponseHeaders(responseHeaders);
        final String contentType = URLConnection.guessContentTypeFromName(resourcePath);
        final String responseBody = readResource(resourcePath);

        return String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode.getStatusCode() + " " + httpStatusCode.getReasonPhrase() + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                headers,
                "",
                responseBody);
    }

    private String parseResponseHeaders(final Map<String, String> responseHeaders) {
        final StringBuilder response = new StringBuilder();
        for (Entry<String, String> entry : responseHeaders.entrySet()) {
            response.append(entry.getKey()).append(": ");
            response.append(entry.getValue()).append(" ");
            response.append("\r\n");
        }
        return response.toString();
    }

    private String handlePostRequest(final String requestTarget, final String messageBody, final Map<String, String> responseHeaders, final Session session) throws IOException {
        if (requestTarget.equals("/login")) {
            final boolean hasLoginSucceeded = loginAndRetrieveUserInfo(messageBody, session);
            if (hasLoginSucceeded) {
                return createRedirectResponse("/index.html", responseHeaders);
            }
            return createRedirectResponse("/401.html", responseHeaders);
        }

        if (requestTarget.equals("/register")) {
            final boolean isRegistered  = registerNewUser(messageBody);
            if (isRegistered) {
                return createRedirectResponse("/index.html", responseHeaders);
            }
            return createForwardResponse(HttpStatusCode.BAD_REQUEST, DEFAULT_RESOURCE_FOLDER + "/register.html", responseHeaders);
        }

        return createForwardResponse(HttpStatusCode.NOT_FOUND, DEFAULT_RESOURCE_FOLDER + "/404.html", responseHeaders);
    }

    private boolean loginAndRetrieveUserInfo(final String messageBody, final Session session) {
        final Map<String, String> loginInfoPairs = parseQuery(messageBody);
        String account = loginInfoPairs.getOrDefault("account", "");
        String password = loginInfoPairs.getOrDefault("password", "");

        if (!account.isBlank() && !password.isBlank()) {
            Optional<User> retrieveResult = InMemoryUserRepository.findByAccount(account);
            if (retrieveResult.isEmpty()) {
                return false;
            }
            final User retrievedUser = retrieveResult.get();
            if (retrievedUser.checkPassword(password)) {
                session.setAttribute("user", retrievedUser);
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

    private String createRedirectResponse(final String redirectURL, final Map<String, String> responseHeaders) {
        final String headers = parseResponseHeaders(responseHeaders);
        return String.join("\r\n",
                "HTTP/1.1 " + HttpStatusCode.FOUND.getStatusCode() + " " + HttpStatusCode.FOUND.getReasonPhrase() + " ",
                "Location: " + redirectURL + " ",
                "Content-Length: 0 ",
                headers,
                "",
                ""
        );
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
