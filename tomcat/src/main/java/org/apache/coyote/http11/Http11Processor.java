package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpStatus;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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

            List<String> lines = new ArrayList<>();

            String line = "";
            while (!(line = bufferedReader.readLine()).equals("")) {
                lines.add(line);
            }
            HttpRequest httpRequest = HttpRequest.from(lines);
            String contentTypeHeader = getContentTypeHeaderFrom(httpRequest);

            RequestHandler requestHandler;
            if (httpRequest.method().equals("POST")) {
                int contentLength = getContentLength(lines);
                String requestBody = readRequestBody(bufferedReader, contentLength);
                requestHandler = handlePostRequest(httpRequest.path(), requestBody);
            } else {
                List<String> cookieHeaderValues = httpRequest.header("Cookie");
                requestHandler = handleGetRequest(httpRequest.method(), httpRequest.path(), cookieHeaderValues);
            }

            String responseBody = readFile(requestHandler.getResponseFilePath());

            List<String> responseHeaders = new ArrayList<>();
            responseHeaders.add(httpRequest.httpVersion() + " " + requestHandler.getHttpStatus() + " ");
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

    private static String getContentTypeHeaderFrom(HttpRequest httpRequest) {
        List<String> acceptHeaderValues = httpRequest.header("Accept");
        if (acceptHeaderValues != null && acceptHeaderValues.contains("text/css")) {
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
        return RequestHandler.of("GET", HttpStatus.NOT_FOUND.getCodeWithMessage(), "static/404.html");
    }

    private RequestHandler handleLoginRequest(String[] splitQueryString) {
        Optional<String> account = getValueOf("account", splitQueryString);
        Optional<String> password = getValueOf("password", splitQueryString);

        if (account.isEmpty() || password.isEmpty()) {
            return RequestHandler.of("GET", HttpStatus.NOT_FOUND.getCodeWithMessage(), "static/401.html");
        }

        Optional<User> findUser = InMemoryUserRepository.findByAccount(account.get());
        if (findUser.isPresent() && findUser.get().checkPassword(password.get())) {
            User user = findUser.get();
            log.info(user.toString());
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user);
            sessionManager.add(session);
            RequestHandler requestHandler = RequestHandler.of("GET", HttpStatus.FOUND.getCodeWithMessage(), "static/index.html");
            requestHandler.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
            return requestHandler;
        }
        return RequestHandler.of("GET", HttpStatus.UNAUTHORIZED.getCodeWithMessage(), "static/401.html");
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
            return RequestHandler.of("GET", HttpStatus.BAD_REQUEST.getCodeWithMessage(), "static/register.html");
        }

        InMemoryUserRepository.save(new User(account.get(), password.get(), email.get()));
        return RequestHandler.of("GET", HttpStatus.FOUND.getCodeWithMessage(), "static/index.html");
    }

    private RequestHandler handleGetRequest(
            String requestMethod,
            String requestUri,
            List<String> cookies
    ) {
        if (!requestMethod.equalsIgnoreCase("GET")) {
            throw new IllegalArgumentException("GET 요청만 처리 가능합니다.");
        }

        if (requestUri.equals("/login.html") || requestUri.equals("/login")) {
            return handleLoginPageRequest(cookies);
        }

        String fileName = "static" + requestUri;
        return RequestHandler.of("GET", HttpStatus.OK.getCodeWithMessage(), fileName);
    }

    private RequestHandler handleLoginPageRequest(List<String> cookies) {
        Optional<String> sessionId = getSessionFrom(cookies);
        if (sessionId.isEmpty()) {
            return RequestHandler.of("GET", HttpStatus.OK.getCodeWithMessage(), "static/login.html");
        }
        Session session = sessionManager.findSession(sessionId.get());
        User user = getUser(session);
        if (InMemoryUserRepository.existsByAccount(user.getAccount())) {
            return RequestHandler.of("GET", HttpStatus.NOT_FOUND.getCodeWithMessage(), "static/index.html");
        }
        return RequestHandler.of("GET", HttpStatus.OK.getCodeWithMessage(), "static/login.html");
    }

    private Optional<String> getSessionFrom(List<String> cookies) {
        return cookies.stream()
                      .filter(cookie -> cookie.startsWith("JSESSIONID="))
                      .map(jsessionid -> jsessionid.substring(jsessionid.indexOf('=') + 1))
                      .findFirst();
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
