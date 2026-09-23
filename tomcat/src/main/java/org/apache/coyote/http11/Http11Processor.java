package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";

    private static final String OK_STATUS_LINE = "HTTP/1.1 200 OK";
    private static final String FOUND_STATUS_LINE = "HTTP/1.1 302 Found";
    private static final String NOT_FOUND_STATUS_LINE = "HTTP/1.1 404 Not Found";

    private static final String NOT_FOUND_PATH = "/404.html";
    private static final String INDEX_PATH = "/index.html";

    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String EMAIL_PARAMETER = "email";

    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";

    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String NOT_FOUND_RESPONSE_BODY = "404 Not Found";

    private static final String JSESSION_ID = "JSESSIONID";
    private static final String CRLF = "\r\n";

    private static final String SESSION_USER_ATTRIBUTE = "user";

    private final Socket connection;
    private final SessionManager sessionManager = SessionManager.getInstance();

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
             final var outputStream = connection.getOutputStream()
        ) {
            final HttpRequest request = readRequest(inputStream);
            sendResponse(request, outputStream);
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readRequest(final InputStream inputStream) throws IOException {
        final BufferedReader reader = getReader(inputStream);
        final List<String> requestHeadLines = readRequestHead(reader);
        final HttpRequest requestHead = HttpRequest.from(requestHeadLines);
        final byte[] requestBody = readRequestBody(reader, requestHead.headers());

        return HttpRequest.of(requestHead.requestLine(), requestHead.headers(), requestBody);
    }

    private List<String> readRequestHead(final BufferedReader reader) throws IOException {
        final List<String> requestHeadLines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            requestHeadLines.add(line);
            if (line.isEmpty()) {
                break;
            }
        }
        return requestHeadLines;
    }

    private byte[] readRequestBody(final BufferedReader reader, final Map<String, String> headers)
            throws IOException {
        final int contentLength = parseContentLength(headers);
        final char[] buffer = new char[contentLength];
        int offset = 0;

        while (offset < contentLength) {
            final int readCount = reader.read(buffer, offset, contentLength - offset);
            if (readCount < 0) {
                throw new IllegalArgumentException("HTTP request body is shorter than Content-Length");
            }
            offset += readCount;
        }
        return new String(buffer).getBytes(StandardCharsets.UTF_8);
    }

    private int parseContentLength(final Map<String, String> headers) {
        final String contentLength = headers.getOrDefault("content-length", "0");
        final int parsedContentLength = Integer.parseInt(contentLength);
        if (parsedContentLength < 0) {
            throw new IllegalArgumentException("Content-Length must not be negative");
        }
        return parsedContentLength;
    }

    private BufferedReader getReader(InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }

    private void sendResponse(
            final HttpRequest request,
            final OutputStream outputStream
    ) throws IOException {

        final RequestLine requestLine = request.requestLine();
        final String requestPath = extractRequestPath(requestLine.requestTarget());
        final HttpMethod method = requestLine.method();

        if (ROOT_PATH.equals(requestPath) && HttpMethod.GET.equals(method)) {
            writeResponse(
                    outputStream,
                    OK_STATUS_LINE,
                    Map.of("Content-Type", HTML_CONTENT_TYPE),
                    readDefaultResponseBody()
            );
            return;
        }

        if (REGISTER_PATH.equals(requestPath) && HttpMethod.GET.equals(method)) {
            byte[] responseBody = findResourceBody(requestPath).get();

            writeResponse(
                    outputStream,
                    OK_STATUS_LINE,
                    Map.of("Content-Type", HTML_CONTENT_TYPE),
                    responseBody
            );
            return;
        }

        if (LOGIN_PATH.equals(requestPath) && HttpMethod.GET.equals(method)) {
            if (isLoggedIn(request)) {
                sendRedirect(outputStream, Map.of("Location", INDEX_PATH));
                return;
            }

            final byte[] responseBody = findResourceBody(requestPath).get();
            writeResponse(
                    outputStream,
                    OK_STATUS_LINE,
                    Map.of("Content-Type", HTML_CONTENT_TYPE),
                    responseBody
            );
            return;
        }

        final Map<String, String> formParameters = parseFormParameters(request.body());
        if (REGISTER_PATH.equals(requestPath) && HttpMethod.POST.equals(method)) {
            register(formParameters);
            sendRedirect(outputStream, Map.of("Location", INDEX_PATH));
            return;
        }

        if (LOGIN_PATH.equals(requestPath) && HttpMethod.POST.equals(method)) {
            final Optional<User> authenticatedUser = findAuthenticatedUser(formParameters);

            if (authenticatedUser.isEmpty()) {
                sendRedirect(outputStream, Map.of("Location", "/401.html"));
                return;
            }

            final User user = authenticatedUser.get();
            log.info("로그인 성공! 아이디 : {}", user.getAccount());

            final Optional<Session> existingSession = findSession(request);
            final Session session = existingSession.orElseGet(this::createSession);
            session.setAttribute(SESSION_USER_ATTRIBUTE, user);

            final Map<String, String> headers = new LinkedHashMap<>();
            headers.put("Location", INDEX_PATH);
            if (existingSession.isEmpty()) {
                headers.put("Set-Cookie", JSESSION_ID + "=" + session.getId());
            }
            sendRedirect(outputStream, headers);
            return;
        }

        final Optional<byte[]> resourceBody = findResourceBody(requestPath);

        if (resourceBody.isEmpty()) {
            writeResponse(
                    outputStream,
                    NOT_FOUND_STATUS_LINE,
                    Map.of("Content-Type", HTML_CONTENT_TYPE),
                    readNotFoundResponseBody()
            );
            return;
        }

        writeResponse(
                outputStream,
                OK_STATUS_LINE,
                Map.of("Content-Type", resolveContentType(requestPath)),
                resourceBody.get()
        );
    }

    private boolean isLoggedIn(final HttpRequest request) {
        return findSession(request)
                .map(session -> session.getAttribute(SESSION_USER_ATTRIBUTE))
                .isPresent();
    }

    private Optional<Session> findSession(final HttpRequest request) {
        final HttpCookie cookie = HttpCookie.from(request.headers().getOrDefault("cookie", ""));

        return cookie.getValue(JSESSION_ID)
                .map(sessionManager::findSession);
    }

    private Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        return session;
    }

    private void sendRedirect(
            final OutputStream outputStream,
            final Map<String, String> headers
    ) throws IOException {
        writeResponse(outputStream, FOUND_STATUS_LINE, headers, new byte[0]);
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String statusLine,
            final Map<String, String> headers,
            final byte[] responseBody
    ) throws IOException {
        final String responseHead = createResponseHead(
                statusLine,
                headers,
                responseBody.length
        );

        outputStream.write(responseHead.getBytes(StandardCharsets.UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private Optional<byte[]> findResourceBody(final String requestPath) throws IOException {
        try (InputStream resourceStream = findResource(requestPath)) {
            if (resourceStream == null) {
                return Optional.empty();
            }
            return Optional.of(resourceStream.readAllBytes());
        }
    }

    private InputStream findResource(final String requestPath) {
        final String resourcePath = resolveResourcePath(requestPath);
        return getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath);
    }

    private byte[] readNotFoundResponseBody() throws IOException {
        return findResourceBody(NOT_FOUND_PATH)
                .orElseGet(() -> NOT_FOUND_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] readDefaultResponseBody() {
        return DEFAULT_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8);
    }

    private String createResponseHead(
            final String statusLine,
            final Map<String, String> headers,
            final int contentLength
    ) {
        final StringBuilder head = new StringBuilder();
        head.append(statusLine).append(" ").append(CRLF);
        headers.forEach((name, value) ->
                head.append(name).append(": ").append(value).append(" ").append(CRLF));
        head.append("Content-Length: ").append(contentLength).append(" ").append(CRLF);
        head.append(CRLF);

        return head.toString();
    }

    private String resolveContentType(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return CSS_CONTENT_TYPE;
        }
        return HTML_CONTENT_TYPE;
    }

    private String extractRequestPath(final String requestTarget) {
        final int queryIndex = requestTarget.indexOf("?");
        if (queryIndex < 0) {
            return requestTarget;
        }

        return requestTarget.substring(0, queryIndex);
    }

    private String resolveResourcePath(final String requestPath) {
        if (requestPath.equals(LOGIN_PATH) || requestPath.equals(REGISTER_PATH)) {
            return "static" + requestPath + ".html";
        }
        return "static" + requestPath;
    }

    private Optional<User> findAuthenticatedUser(final Map<String, String> formParameters) {
        if (!hasCredentials(formParameters)) {
            return Optional.empty();
        }

        final String account = formParameters.get(ACCOUNT_PARAMETER);
        final String password = formParameters.get(PASSWORD_PARAMETER);

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private boolean hasCredentials(final Map<String, String> formParameters) {
        return formParameters.containsKey(ACCOUNT_PARAMETER) && formParameters.containsKey(PASSWORD_PARAMETER);
    }

    private void register(final Map<String, String> formParameters) {
        final String account = getRequiredParameter(formParameters, ACCOUNT_PARAMETER);
        final String password = getRequiredParameter(formParameters, PASSWORD_PARAMETER);
        final String email = getRequiredParameter(formParameters, EMAIL_PARAMETER);

        InMemoryUserRepository.save(new User(account, password, email));
    }

    private String getRequiredParameter(final Map<String, String> formParameters, final String name) {
        final String value = formParameters.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing form parameter: " + name);
        }
        return value;
    }

    private Map<String, String> parseFormParameters(final byte[] body) {
        final String formData = new String(body, StandardCharsets.UTF_8);

        if (formData.isBlank()) {
            return Map.of();
        }
        final Map<String, String> parameters = new HashMap<>();
        for (final String param : formData.split("&")) {
            final String[] keyAndValue = param.split("=", 2);
            validateQueryParameter(keyAndValue);
            final String value = URLDecoder.decode(keyAndValue[1], StandardCharsets.UTF_8);
            parameters.put(keyAndValue[0], value);
        }
        return Map.copyOf(parameters);
    }

    private void validateQueryParameter(final String[] keyAndValue) {
        if (keyAndValue.length != 2) {
            throw new IllegalArgumentException("Invalid query parameter");
        }
    }
}
