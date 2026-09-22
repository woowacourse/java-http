package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_URI = "/";
    private static final String STATIC_RESOURCE_ROOT = "static";
    private static final String LOGIN = "/login";
    private static final String REGISTER = "/register";
    private static final String POST_METHOD = "POST";
    private static final String LOGIN_SUCCESS = "/index.html";
    private static final String LOGIN_FAILURE = "/401.html";
    private static final String DOT_HTML = ".html";

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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }
            final String[] requestParts = requestLine.split(" ");
            final String method = requestParts[0];
            String requestUri = requestParts[1];
            final String version = requestParts[2];
            final Map<String, String> headers = readHeaders(reader);
            final String requestBody = readRequestBody(method, headers, reader);

            byte[] responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
            final String requestPath = extractRequestPath(requestUri);

            if (method.equals(POST_METHOD) && requestPath.equals(LOGIN)) {
                final String location = getLoginRedirectionLocation(requestBody);
                final String responseHeader = createRedirectResponseHeader(version, location);

                outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            if (method.equals(POST_METHOD) && requestPath.equals(REGISTER)) {
                saveUser(requestBody);
                final String responseHeader = createRedirectResponseHeader(version, LOGIN_SUCCESS);

                outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            requestUri = requestPath;

            if (requestUri.equals(LOGIN) || requestUri.equals(REGISTER)) {
                requestUri += DOT_HTML;
            }

            String contentType = getContentType(requestUri);

            if (!requestUri.equals(ROOT_URI)) {
                final Path path = Path.of(getResourcePath(STATIC_RESOURCE_ROOT + requestUri));
                responseBody = Files.readAllBytes(path);
            }

            final String responseHeader = createResponseHeader(version, contentType, responseBody.length);

            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            final String[] header = line.split(":", 2);
            final String name = header[0].trim();
            final String value = header[1].trim();
            headers.put(name, value);
        }
        return headers;
    }

    private String readRequestBody(final String method,
                                   final Map<String, String> headers,
                                   final BufferedReader reader) throws IOException {
        if (!method.equals(POST_METHOD)) {
            return "";
        }

        final int contentLength = Integer.parseInt(headers.get("Content-Length"));
        final char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private String createResponseHeader(final String version, String contentType, final int contentLength) {
        return String.join("\r\n",
                version + " 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + contentLength + " ",
                "",
                "");
    }

    private String createRedirectResponseHeader(final String version, final String location) {
        return String.join("\r\n",
                version + " 302 Found ",
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "",
                "");
    }

    private String getResourcePath(final String path) {
        final URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new UncheckedServletException(
                    new FileNotFoundException("리소스를 찾을 수 없습니다: " + path));
        }
        return resource.getPath();
    }

    private String getContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html;charset=utf-8";
    }

    private boolean hasQueryString(final String requestUri) {
        return requestUri.indexOf('?') != -1;
    }

    private String extractRequestPath(final String requestUri) {
        if (!hasQueryString(requestUri)) {
            return requestUri;
        }

        return requestUri.substring(0, requestUri.indexOf('?'));
    }

    private String getLoginRedirectionLocation(final String requestBody) {
        final String[] formParts = requestBody.split("&");

        if (checkUser(formParts)) {
            return LOGIN_SUCCESS;
        }

        return LOGIN_FAILURE;
    }

    private void saveUser(final String requestBody) {
        final Map<String, String> formData = parseFormData(requestBody);
        final User user = new User(
                formData.get("account"),
                formData.get("password"),
                formData.get("email")
        );
        InMemoryUserRepository.save(user);
    }

    private Map<String, String> parseFormData(final String requestBody) {
        final Map<String, String> formData = new HashMap<>();
        final String[] formFields = requestBody.split("&");

        for (String formField : formFields) {
            final String[] keyValue = formField.split("=", 2);
            if (keyValue.length < 2) {
                continue;
            }
            formData.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
        }
        return formData;
    }

    private boolean checkUser(final String[] requestParts) {
        if (requestParts.length < 2) {
            return false;
        }

        final String[] accountPart = requestParts[0].split("=", 2);
        final String[] passwordPart = requestParts[1].split("=", 2);
        if (accountPart.length < 2 || passwordPart.length < 2) {
            return false;
        }

        final String account = accountPart[1];
        final String password = passwordPart[1];

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty()) {
            return false;
        }

        if (user.get().checkPassword(password)) {
            log.info("로그인 성공: {}", user.get());
            return true;
        }

        return false;
    }
}
