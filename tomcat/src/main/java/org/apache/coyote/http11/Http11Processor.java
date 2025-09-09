package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
             final var outputStream = connection.getOutputStream()) {

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            final String[] requestInfo = parseRequestLine(requestLine);
            if (requestInfo == null) {
                return;
            }

            final Map<String, String> requestHeaders = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                final String[] headerParts = line.split(": ", 2);
                if (headerParts.length == 2) {
                    requestHeaders.put(headerParts[0], headerParts[1]);
                }
            }

            String requestBody = "";
            if ("POST".equals(requestInfo[0]) && requestHeaders.containsKey("Content-Length")) {
                int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                requestBody = new String(buffer);
            }

            final String response = handleRequest(requestInfo, requestHeaders, requestBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String[] parseRequestLine(final String requestLine) {
        final String[] requestLineArray = requestLine.split(" ");
        if (requestLineArray.length < 2) {
            return null;
        }

        final String method = requestLineArray[0];
        final String uri = requestLineArray[1];

        return new String[]{method, uri};
    }

    private String handleRequest(final String[] requestInfo, final Map<String, String> requestHeaders,
                                 final String requestBody) throws IOException, URISyntaxException {
        final String method = requestInfo[0];
        final String path = requestInfo[1];

        final HttpCookie cookie = new HttpCookie(requestHeaders.get("Cookie"));
        final String existingJSessionId = cookie.getJSessionId();

        final Session session = (existingJSessionId != null) ? sessionManager.findSession(existingJSessionId) : null;
        final User user = (session != null) ? (User) session.getAttribute("user") : null;

        if ("/login".equals(path)) {
            if (user != null && "GET".equals(method)) {
                return generateRedirectResponse("/index.html", existingJSessionId);
            }
            return handleLogin(method, requestBody);
        }
        if ("/register".equals(path)) {
            return handleRegister(method, requestBody);
        }

        return serveStaticFile(path, existingJSessionId);
    }

    private String handleLogin(final String method, final String requestBody) throws IOException, URISyntaxException {
        if ("POST".equals(method)) {
            return processLogin(requestBody);
        }
        return serveStaticFile("/login.html", null);
    }

    private String handleRegister(final String method, final String requestBody)
            throws IOException, URISyntaxException {
        if ("POST".equals(method)) {
            return processRegister(requestBody);
        }
        return serveStaticFile("/register.html", null);
    }

    private String processLogin(final String requestBody) {
        final Map<String, String> parameters = parseFormData(requestBody);
        final boolean loginSuccess = authenticateUser(parameters);

        if (loginSuccess) {
            final String jSessionId = UUID.randomUUID().toString();
            final User user = InMemoryUserRepository.findByAccount(parameters.get("account")).get();

            Session newSession = new Session(jSessionId);
            newSession.setAttribute("user", user);
            sessionManager.add(newSession);

            return generateRedirectResponse("/index.html", jSessionId);
        }

        return generateRedirectResponse("/401.html", null);
    }

    private String processRegister(final String requestBody) {
        final Map<String, String> parameters = parseFormData(requestBody);
        final boolean registerSuccess = registerUser(parameters);

        final String redirectLocation = registerSuccess ? "/index.html" : "/register.html";

        return generateRedirectResponse(redirectLocation, null);
    }

    private boolean authenticateUser(final Map<String, String> parameters) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");

        if (account == null || password == null) {
            return false;
        }

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElse(null);
        if (user == null) {
            return false;
        }

        if (user.checkPassword(password)) {
            log.info("user: {}", user);
            return true;
        }
        return false;
    }

    private boolean registerUser(final Map<String, String> parameters) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        final String email = parameters.get("email");

        if (account == null || password == null || email == null) {
            return false;
        }

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return false;
        }
        try {
            final User newUser = new User(account, password, email);
            InMemoryUserRepository.save(newUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String serveStaticFile(final String path, final String existingJSessionId)
            throws IOException, URISyntaxException {
        final byte[] fileBytes = readFile(path);

        return generateOkResponse(path, fileBytes, existingJSessionId);
    }

    private Map<String, String> parseFormData(final String formData) {
        final Map<String, String> parameters = new HashMap<>();
        if (formData != null && !formData.isEmpty()) {
            final String[] pairs = formData.split("&");
            for (final String pair : pairs) {
                final String[] keyValue = pair.split("=");
                if (keyValue.length >= 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return parameters;
    }

    private String generateRedirectResponse(final String location, final String jSessionId) {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 302 Found\r\n");
        response.append("Location: ").append(location).append("\r\n");
        response.append("Content-Type: text/html; charset=UTF-8\r\n");
        response.append("Content-Length: 0\r\n");

        if (jSessionId != null) {
            response.append("Set-Cookie: JSESSIONID=").append(jSessionId).append("\r\n");
        }

        response.append("\r\n");
        return response.toString();
    }

    private byte[] readFile(final String path) throws IOException, URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource("static" + path);
        final Path filePath = Paths.get(resource.toURI());
        return Files.readAllBytes(filePath);
    }

    private String generateOkResponse(final String path, final byte[] bytes, final String jSessionId) {
        final String responseBody = new String(bytes);
        final String contentType = getContentType(path);

        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 200 OK\r\n");
        response.append("Content-Type: ").append(contentType).append(";charset=utf-8\r\n");
        response.append("Content-Length: ").append(bytes.length).append("\r\n");

        if (jSessionId != null) {
            response.append("Set-Cookie: JSESSIONID=").append(jSessionId).append("\r\n");
        }

        response.append("\r\n");
        response.append(responseBody);

        return response.toString();
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "application/javascript";
        }
        return "text/html";
    }
}
