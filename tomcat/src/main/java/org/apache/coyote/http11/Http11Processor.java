package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final byte[] HELLO_WORLD = "Hello world!".getBytes(StandardCharsets.UTF_8);
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String GET = "GET";
    private static final String POST = "POST";

    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";

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
        try (final BufferedInputStream inputStream =
                     new BufferedInputStream(connection.getInputStream());
             final OutputStream outputStream =
                     connection.getOutputStream()) {

            // 1. Request Line
            final String requestLine = readLine(inputStream);
            if (requestLine == null) {
                return;
            }

            // POST /login HTTP/1.1
            final String method = extractMethod(requestLine);
            final String uri = extractUri(requestLine);

            if (method == null || uri == null) {
                return;
            }

            // 2. HTTP Headers
            final Map<String, String> headers =
                    readHeaders(inputStream);

            // 3. Request Body
            final String requestBody =
                    readRequestBody(inputStream, headers);


            // 4. path와 query string 분리
            final String path = extractPath(uri);
            final String queryString = extractQueryString(uri);


            // 5. 로그인 처리
            if (handleLogin(
                    outputStream,
                    method,
                    path,
                    queryString,
                    requestBody
            )) {
                return;
            }

            // 6. 회원가입 처리
            if (handleRegister(
                    outputStream,
                    method,
                    path,
                    requestBody
            )) {
                return;
            }


            // 7. 기본
            if ("/".equals(path)) {
                writeResponse(
                        outputStream,
                        "200 OK",
                        "text/html;charset=utf-8",
                        HELLO_WORLD
                );
                return;
            }
            // 8. 정적
            writeStaticResource(outputStream, path);

        } catch (IOException
                 | URISyntaxException
                 | UncheckedServletException e) {

            log.error(e.getMessage(), e);
        }
    }

    private String readLine(final BufferedInputStream inputStream) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int value;
        while ((value = inputStream.read()) != -1) {
            if (value == '\n') {
                break;
            }
            if (value != '\r') {
                buffer.write(value);
            }
        }

        if (value == -1 && buffer.size() == 0) {
            return null;
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    private Map<String, String> readHeaders(
            final BufferedInputStream inputStream
    ) throws IOException {

        final Map<String, String> headers =
                new HashMap<>();

        String line;

        while ((line = readLine(inputStream)) != null) {

            // Header와 Body 사이의 빈 줄
            if (line.isEmpty()) {
                break;
            }

            final int colonIndex = line.indexOf(":");

            if (colonIndex == -1) {
                continue;
            }

            final String name =
                    line.substring(0, colonIndex).trim();

            final String value =
                    line.substring(colonIndex + 1).trim();

            headers.put(name, value);
        }

        return headers;
    }

    private String readRequestBody(
            final BufferedInputStream inputStream,
            final Map<String, String> headers
    ) throws IOException {

        final String contentLengthValue =
                headers.get(CONTENT_LENGTH);// 바이트 수

        if (contentLengthValue == null) {
            return "";
        }

        final int contentLength;

        try {
            contentLength =
                    Integer.parseInt(contentLengthValue);
        } catch (NumberFormatException e) {
            return "";
        }

        if (contentLength <= 0) {
            return "";
        }

        final byte[] body =
                inputStream.readNBytes(contentLength);

        return new String(
                body,
                StandardCharsets.UTF_8
        );
    }


    private String extractMethod(
            final String requestLine
    ) {
        final String[] parts =
                requestLine.split(" ", 3);

        if (parts.length < 3) {
            return null;
        }

        return parts[0];
    }


    private String extractUri(final String requestLine) {
        final String[] parts = requestLine.split(" ", 3);

        if (parts.length < 2) {
            return null;
        }

        return parts[1];
    }

    private String extractPath(final String uri) {
        final int queryIndex = uri.indexOf("?");

        if (queryIndex == -1) {
            return uri;
        }

        return uri.substring(0, queryIndex);
    }

    private String extractQueryString(final String uri) {
        final int queryIndex = uri.indexOf("?");

        if (queryIndex == -1) {
            return null;
        }

        return uri.substring(queryIndex + 1);
    }

    private boolean handleLogin(
            final OutputStream outputStream,
            final String method,
            final String path,
            final String queryString,
            final String requestBody
    ) throws IOException {
        if (!"/login".equals(path)) {
            return false;
        }


        final String parameterString;

        if ("POST".equals(method)) {
            parameterString = requestBody;
        } else {
            parameterString = queryString;
        }

        // GET /login 처럼 로그인 정보 없이 로그인 페이지 자체를 요청한 경우
        if (parameterString == null
                || parameterString.isBlank()) {
            return false;
        }


        final Map<String, String> parameters =
                parseParameters(parameterString);

        final String account = parameters.get("account");
        final String password = parameters.get("password");

        if (account == null || password == null) {
            writeRedirect(
                    outputStream,
                    "/401.html"
            );
            return true;
        }

        final Optional<User> user =
                InMemoryUserRepository.findByAccount(account);

        final boolean loginSuccess =
                user.filter(foundUser ->
                                foundUser.checkPassword(password))
                        .isPresent();


        if (loginSuccess) {
            log.info(
                    "login success account: {}",
                    account
            );

            writeRedirect(
                    outputStream,
                    "/index.html"
            );
            return true;
        }
        log.info(
                "login failed account: {}",
                account
        );

        writeRedirect(
                outputStream,
                "/401.html"
        );

        return true;

    }


    private boolean handleRegister(
            final OutputStream outputStream,
            final String method,
            final String path,
            final String requestBody
    ) throws IOException {

        if (!REGISTER_PATH.equals(path)) {
            return false;
        }

        if (GET.equals(method)) {
            return false;
        }

        if (!POST.equals(method)) {
            return false;
        }

        final Map<String, String> parameters = parseParameters(requestBody);

        final String account = parameters.get("account");

        final String password = parameters.get("password");

        final String email = parameters.get("email");

        if (account == null || password == null || email == null) {
            return false;
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        log.info("register success account: {}", account);

        writeRedirect(outputStream, "/index.html");

        return true;
    }


    private void writeRedirect(
            final OutputStream outputStream,
            final String location
    ) throws IOException {

        final String responseHeaders =
                String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: " + location + " ",
                        "Content-Length: 0 ",
                        "",
                        ""
                );

        outputStream.write(
                responseHeaders.getBytes(
                        StandardCharsets.UTF_8
                )
        );

        outputStream.flush();
    }

    private Map<String, String> parseParameters(
            final String queryString
    ) {
        final Map<String, String> parameters =
                new HashMap<>();

        for (String parameter : queryString.split("&")) {
            final String[] pair = parameter.split("=", 2);

            if (pair.length != 2) {
                continue;
            }

            parameters.put(decode(pair[0]), decode(pair[1]));
        }

        return parameters;
    }

    private String decode(
            final String value
    ) {
        return URLDecoder.decode(
                value,
                StandardCharsets.UTF_8
        );
    }

    private void writeStaticResource(
            final OutputStream outputStream,
            final String path
    ) throws IOException, URISyntaxException {

        final String resourcePath = resolveResourcePath(path);

        final URL resource = getClass()
                .getClassLoader()
                .getResource(resourcePath);
        if (resource == null) {
            writeNotFound(outputStream);
            return;
        }
        final byte[] responseBody =
                Files.readAllBytes(
                        Path.of(resource.toURI())
                );


        writeResponse(
                outputStream,
                "200 OK",
                resolveContentType(path),
                responseBody
        );
    }

    private void writeNotFound(
            final OutputStream outputStream
    ) throws IOException {

        final byte[] responseBody =
                "Not Found".getBytes(
                        StandardCharsets.UTF_8
                );

        writeResponse(
                outputStream,
                "404 Not Found",
                "text/plain;charset=utf-8",
                responseBody
        );
    }

    private String resolveResourcePath(final String path) {
        if ("/login".equals(path)) {
            return "static/login.html";
        }
        if (REGISTER_PATH.equals(path)) {
            return "static/register.html";
        }

        return "static" + path;
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }

        if (path.endsWith(".js")) {
            return "application/javascript";
        }

        return "text/html;charset=utf-8";
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String status,
            final String contentType,
            final byte[] responseBody
    ) throws IOException {

        final String responseHeaders =
                String.join("\r\n",
                        "HTTP/1.1 " + status + " ",
                        "Content-Type: "
                                + contentType + " ",
                        "Content-Length: "
                                + responseBody.length + " ",
                        "",
                        ""
                );

        outputStream.write(
                responseHeaders.getBytes(
                        StandardCharsets.UTF_8
                )
        );

        outputStream.write(responseBody);
        outputStream.flush();
    }
}

