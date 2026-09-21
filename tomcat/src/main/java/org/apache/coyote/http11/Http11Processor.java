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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private static final String NOT_FOUND_PATH = "/404.html";
    private static final String OK_STATUS_LINE = "HTTP/1.1 200 OK";
    private static final String FOUND_STATUS_LINE = "HTTP/1.1 302 Found";
    private static final String NOT_FOUND_STATUS_LINE = "HTTP/1.1 404 Not Found";
    private static final String INDEX_PATH = "/index.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String CONTENT_LENGTH_HEADER = "content-length";
    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String EMAIL_PARAMETER = "email";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String NOT_FOUND_RESPONSE_BODY = "404 Not Found";

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
        final String contentLength = headers.getOrDefault(CONTENT_LENGTH_HEADER, "0");
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

    private void sendResponse(final HttpRequest request, final OutputStream outputStream) throws IOException {
        final RequestLine requestLine = request.requestLine();
        final String requestPath = extractRequestPath(requestLine.requestTarget());
        final HttpMethod method = requestLine.method();

        if (ROOT_PATH.equals(requestPath) && HttpMethod.GET.equals(method)) {
            writeResponse(
                    outputStream,
                    OK_STATUS_LINE,
                    HTML_CONTENT_TYPE,
                    readDefaultResponseBody()
            );
            return;
        }

        if (REGISTER_PATH.equals(requestPath) && HttpMethod.GET.equals(method)) {
            byte[] responseBody = findResourceBody(requestPath).get();

            writeResponse(
                    outputStream,
                    OK_STATUS_LINE,
                    HTML_CONTENT_TYPE,
                    responseBody
            );
            return;
        }

        if (LOGIN_PATH.equals(requestPath) && HttpMethod.GET.equals(method)) {
            byte[] responseBody = findResourceBody(requestPath).get();

            writeResponse(
                    outputStream,
                    OK_STATUS_LINE,
                    HTML_CONTENT_TYPE,
                    responseBody
            );
            return;
        }

        final Map<String, String> formParameters = parseFormParameters(request.body());

        if (REGISTER_PATH.equals(requestPath) && HttpMethod.POST.equals(method)) {
            register(formParameters);
            sendRedirect(outputStream, INDEX_PATH);
            return;
        }

        if (LOGIN_PATH.equals(requestPath) && HttpMethod.POST.equals(method)) {
            if (checkAuthentication(formParameters)) {
                log.info("로그인 성공! 아이디 : {}", formParameters.get(ACCOUNT_PARAMETER));
                sendRedirect(outputStream, INDEX_PATH);
                return;
            }
            sendRedirect(outputStream, UNAUTHORIZED_PATH);
            return;
        }

        final Optional<byte[]> resourceBody = findResourceBody(requestPath);

        if (resourceBody.isEmpty()) {
            writeResponse(
                    outputStream,
                    NOT_FOUND_STATUS_LINE,
                    HTML_CONTENT_TYPE,
                    readNotFoundResponseBody()
            );
            return;
        }

        writeResponse(
                outputStream,
                OK_STATUS_LINE,
                resolveContentType(requestPath),
                resourceBody.get()
        );
    }

    private void sendRedirect(final OutputStream outputStream, final String location) throws IOException {
        final String response = String.join("\r\n",
                FOUND_STATUS_LINE + " ",
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "",
                "");

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String statusLine,
            final String contentType,
            final byte[] responseBody
    ) throws IOException {
        final String responseHead = createResponseHead(
                statusLine,
                contentType,
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
            final String contentType,
            final int contentLength
    ) {
        return String.join("\r\n",
                statusLine + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + contentLength + " ",
                "",
                "");
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

    private boolean checkAuthentication(final Map<String, String> queryParameters) {
        if (!hasCredentials(queryParameters)) {
            return false;
        }

        final String account = queryParameters.get(ACCOUNT_PARAMETER);
        final String password = queryParameters.get(PASSWORD_PARAMETER);

        return InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }

    private boolean hasCredentials(final Map<String, String> queryParameters) {
        return queryParameters.containsKey(ACCOUNT_PARAMETER) && queryParameters.containsKey(PASSWORD_PARAMETER);
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
            parameters.put(decode(keyAndValue[0]), decode(keyAndValue[1]));
        }
        return Map.copyOf(parameters);
    }

    private String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private void validateQueryParameter(final String[] keyAndValue) {
        if (keyAndValue.length != 2) {
            throw new IllegalArgumentException("Invalid query parameter");
        }
    }
}
