package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
             final var outputStream = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            String[] requestParts = requestLine.split(" ");

            String method = requestParts[0];
            String requestUri = requestParts[1];

            Map<String, String> headers = readHeaders(reader);

            HttpCookie cookie = new HttpCookie(headers.get("Cookie"));
            String sessionId = cookie.get("JSESSIONID");
            boolean newSession = !cookie.contains("JSESSIONID");

            String setCookieHeader = "";
            if (newSession) {
                sessionId = UUID.randomUUID().toString();
                setCookieHeader = "Set-Cookie: JSESSIONID=" + sessionId + "\r\n";
            }

            String requestBody = "";
            if (method.equals("POST")) {
                String contentLengthHeader = headers.get("Content-Length");
                if (contentLengthHeader != null) {
                    int contentLength = Integer.parseInt(contentLengthHeader);
                    char[] buffer = new char[contentLength];

                    reader.read(buffer, 0, contentLength);
                    requestBody = new String(buffer);
                }
            }

            String path = parsePath(requestUri);

            String queryString = parseQueryString(requestUri);

            String  parameters;
            if (method.equals("POST")) {
                parameters = requestBody;
            } else {
                parameters = queryString;
            }

            String redirectLocation = handleLogin(path, parameters);
            if (redirectLocation == null) {
                redirectLocation = handleRegister(method, path, parameters);
            }

            if (redirectLocation != null) {
                final var response =
                        "HTTP/1.1 302 Found\r\n"
                                + "Location: " + redirectLocation + "\r\n"
                                + setCookieHeader
                                + "Content-Length: 0\r\n"
                                + "\r\n";

                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            byte[] responseBody = readResponseBody(path);
            String contentType = resolveContentType(path);

            final var response =
                    "HTTP/1.1 200 OK\r\n"
                            + setCookieHeader
                            + "Content-Type: " + contentType + "charset=utf-8\r\n"
                            + "Content-Length: " + responseBody.length + "\r\n"
                            + "\r\n";

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String handleLogin(String path, String parameters) {
        if (!"/login".equals(path) || parameters.isEmpty()) {
            return null;
        }

        Map<String, String> parametersByName = parseParameters(parameters);

        String account = parametersByName.get("account");
        String password = parametersByName.get("password");

        if (account == null || password == null) {
            return "/401.html";
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> "/index.html")
                .orElse("/401.html");
    }

    private Map<String, String> parseParameters(String parameters) {
        Map<String, String> parametersByName = new HashMap<>();
        String[] parameterPairs = parameters.split("&");

        for (String parameter : parameterPairs) {
            String[] nameAndValue = parameter.split("=");

            if (nameAndValue.length != 2) {
                return Map.of();
            }
            parametersByName.put(nameAndValue[0], nameAndValue[1]);
        }

        return parametersByName;
    }

    private String handleRegister(String method, String path, String parameters) {
        if (!method.equals("POST") || !"/register".equals(path) || parameters.isEmpty()) {
            return null;
        }

        Map<String, String> parametersByName = parseParameters(parameters);

        String account = parametersByName.get("account");
        String password = parametersByName.get("password");
        String email = parametersByName.get("email");

        if (account == null || password == null || email == null) {
            return null;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        return "/index.html";
    }

    private String parsePath(String requestUri) {
        int queryIndex = requestUri.indexOf("?");
        if (queryIndex >= 0) {
            return requestUri.substring(0, queryIndex);
        }
        return requestUri;
    }

    private String parseQueryString(String requestUri) {
        int queryIndex = requestUri.indexOf("?");
        if (queryIndex >= 0) {
            return requestUri.substring(queryIndex + 1);
        }

        return "";
    }

    private String resolveContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;";
        }

        return "text/html;";
    }

    private byte[] readResponseBody(String requestUri) throws IOException {
        if ("/".equals(requestUri)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }

        if ("/login".equals(requestUri) || "/register".equals(requestUri)) {
            requestUri = requestUri + ".html";
        }

        String resourceName = "static" + requestUri;
        try (InputStream resourceStream =
                     Http11Processor.class
                             .getClassLoader()
                             .getResourceAsStream(resourceName)) {

            return Objects.requireNonNull(resourceStream).readAllBytes();
        }
    }

    private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        while (true) {
            String headerLine = reader.readLine();

            if (headerLine == null || headerLine.isEmpty()) {
                break;
            }

            String[] parts = headerLine.split(":", 2);

            String left = parts[0].strip();
            String right = parts[1].strip();

            headers.put(left, right);
        }

        return headers;
    }
}
