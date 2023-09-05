package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String SAFARI_CHROME_ACCEPT_HEADER_DEFAULT_VALUE = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8";

    private final Socket connection;
    private final SessionManager sessionManager = new SessionManager();

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
        try (var inputStream = connection.getInputStream();
             var outputStream = connection.getOutputStream()) {

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            List<String> headers = new ArrayList<>();

            String header = "";
            while (!(header = bufferedReader.readLine()).equals("")) {
                headers.add(header);
            }
            String[] splitStatusLine = Objects.requireNonNull(headers.get(0)).split(" ");
            String requestMethod = splitStatusLine[0];
            String requestUri = splitStatusLine[1];

            String requestAcceptHeader = findHeader("Accept", headers);
            String contentTypeHeader = getContentTypeHeaderFrom(requestAcceptHeader);

            RequestHandler requestHandler;
            if (requestMethod.equalsIgnoreCase("POST")) {
                int contentLength = getContentLength(headers);
                String requestBody = readRequestBody(bufferedReader, contentLength);
                requestHandler = handlePostRequest(requestUri, requestBody);
            } else {
                String cookieHeader = findHeader("Cookie", headers);
                requestHandler = handleGetRequest(requestMethod, requestUri, cookieHeader);
            }

            String responseBody = readFile(requestHandler.getResponseFilePath());

            List<String> responseHeaders = new ArrayList<>();
            responseHeaders.add("HTTP/1.1 " + requestHandler.getHttpStatus() + " ");
            responseHeaders.add(contentTypeHeader);
            responseHeaders.add("Content-Length: " + responseBody.getBytes().length + " ");
            for (Entry<String, String> headerEntry : requestHandler.getHeaders().entrySet()) {
                responseHeaders.add(headerEntry.getKey() + ": " + headerEntry.getValue());
            }
            String responseHeader = String.join("\r\n", responseHeaders);

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

            var response = String.join("\r\n", responseHeader, "", responseBody);

            bufferedOutputStream.write(response.getBytes());
            bufferedOutputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String findHeader(String key, List<String> headers) {
        return headers.stream()
                      .filter(it -> it.startsWith(key + ": "))
                      .findFirst()
                      .orElse("Accept: " + SAFARI_CHROME_ACCEPT_HEADER_DEFAULT_VALUE);
    }

    private static String getContentTypeHeaderFrom(String requestAcceptHeader) {
        String[] splitAcceptHeader = requestAcceptHeader.split(" ");
        String headerValue = splitAcceptHeader[1];
        String[] acceptTypes = headerValue.split(";");
        String[] splitAcceptTypes = acceptTypes[0].split(",");

        if (Arrays.asList(splitAcceptTypes).contains("text/css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }
        return "Content-Type: text/html;charset=utf-8 ";
    }

    private int getContentLength(List<String> headers) {
        Optional<String> contentLengthHeader = headers.stream()
                                                      .filter(it -> it.startsWith("Content-Length"))
                                                      .findFirst();

        if (contentLengthHeader.isEmpty()) {
            return -1;
        }
        int index = contentLengthHeader.get().indexOf(" ");
        return Integer.parseInt(contentLengthHeader.get().substring(index + 1));
    }

    private static String readRequestBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private RequestHandler handlePostRequest(String requestUri, String requestBody) {
        String[] splitRequestBody = requestBody.split("&");
        if (requestUri.equals("/login")) {
            return handleLoginRequest(splitRequestBody);
        }
        if (requestUri.equals("/register")) {
            return handleRegisterRequest(splitRequestBody);
        }
        return RequestHandler.of("GET", "404 Not Found", "static/404.html");
    }

    private RequestHandler handleLoginRequest(String[] splitQueryString) {
        Optional<String> account = getValueOf("account", splitQueryString);
        Optional<String> password = getValueOf("password", splitQueryString);

        if (account.isEmpty() || password.isEmpty()) {
            return RequestHandler.of("GET", "400 Bad Request", "static/401.html");
        }

        Optional<User> findUser = InMemoryUserRepository.findByAccount(account.get());
        if (findUser.isPresent() && findUser.get().checkPassword(password.get())) {
            User user = findUser.get();
            log.info(user.toString());
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user);
            sessionManager.add(session);
            RequestHandler requestHandler = RequestHandler.of("GET", "302 Found", "static/index.html");
            requestHandler.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
            return requestHandler;
        }
        return RequestHandler.of("GET", "401 Unauthorized", "static/401.html");
    }

    private Optional<String> getValueOf(String key, String[] splitQueryString) {
        return Arrays.stream(splitQueryString)
                     .filter(it -> equalsKey(key, it))
                     .map(it -> it.substring(it.indexOf("=") + 1))
                     .findFirst();
    }

    private boolean equalsKey(String expected, String actual) {
        String[] splitActual = actual.split("=");
        return splitActual[0].equals(expected);
    }

    private RequestHandler handleRegisterRequest(String[] splitQueryString) {
        Optional<String> account = getValueOf("account", splitQueryString);
        Optional<String> email = getValueOf("email", splitQueryString);
        Optional<String> password = getValueOf("password", splitQueryString);

        if (account.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return RequestHandler.of("GET", "400 Bad Request", "static/register.html");
        }

        InMemoryUserRepository.save(new User(account.get(), password.get(), email.get()));
        return RequestHandler.of("GET", "302 Found", "static/index.html");
    }

    private RequestHandler handleGetRequest(String requestMethod, String requestUri, String cookie) throws IOException {
        if (!requestMethod.equalsIgnoreCase("GET")) {
            throw new IllegalArgumentException("GET 요청만 처리 가능합니다.");
        }

        if (requestUri.equals("/login.html") || requestUri.equals("/login")) {
            return handleLoginPageRequest(cookie);
        }

        String fileName = "static" + requestUri;
        return RequestHandler.of("GET", "200 OK", fileName);
    }

    private RequestHandler handleLoginPageRequest(String cookie) throws IOException {
        Optional<String> sessionId = getSessionId(cookie);
        if (sessionId.isEmpty()) {
            return RequestHandler.of("GET", "200 OK", "static/login.html");
        }
        Session session = sessionManager.findSession(sessionId.get());
        User user = getUser(session);
        if (InMemoryUserRepository.existsByAccount(user.getAccount())) {
            return RequestHandler.of("GET", "302 Found", "static/index.html");
        }
        return RequestHandler.of("GET", "200 OK", "static/login.html");
    }

    private Optional<String> getSessionId(String cookieHeader) {
        String[] splitCookie = cookieHeader.split(" ");
        return getValueOf("JSESSIONID", splitCookie);
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }

    private String readFile(String filePath) {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.collect(Collectors.joining("\n", "", "\n"));
        } catch (IOException | UncheckedIOException e) {
            return "Hello world!";
        }
    }
}
