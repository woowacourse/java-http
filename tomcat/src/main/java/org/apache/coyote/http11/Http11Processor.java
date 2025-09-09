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
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            while((line = reader.readLine()) != null && !line.isEmpty()) {
                final String[] headerParts = line.split(": ", 2);
                if (headerParts.length == 2) {
                    requestHeaders.put(headerParts[0], headerParts[1]);
                }
            }

            String requestBody = "";
            if("POST".equals(requestInfo[0]) && requestHeaders.containsKey("Content-Length")) {
                int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                requestBody = new String(buffer);
            }

            final String response = handleRequest(requestInfo, requestBody);

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

    private String handleRequest(final String[] requestInfo, final String requestBody) throws IOException, URISyntaxException {
        String method = requestInfo[0];
        String path = requestInfo[1];

        if ("/login".equals(path)) {
            return handleLogin(method, requestBody);
        }
        if("/register".equals(path)) {
            return handleRegister(method, requestBody);
        }

        return serveStaticFile(path);
    }

    private String handleLogin(final String method, final String requestBody) throws IOException, URISyntaxException {
        if("POST".equals(method)) {
            return processLogin(requestBody);
        }
        return serveStaticFile( "/login.html");
    }

    private String handleRegister(final String method, final String requestBody) throws IOException, URISyntaxException {
        if("POST".equals(method)) {
            return processRegister(requestBody);
        }
        return serveStaticFile("/register.html");
    }

    private String processLogin(final String requestBody) {
        final Map<String, String> parameters = parseFormData(requestBody);
        final boolean loginSuccess = authenticateUser(parameters);

        final String redirectLocation = loginSuccess ? "/index.html" : "/401.html";

        return generateRedirectResponse(redirectLocation);
    }

    private String processRegister(final String requestBody) {
        final Map<String, String> parameters = parseFormData(requestBody);
        final boolean registerSuccess = registerUser(parameters);

        final String redirectLocation = registerSuccess ? "/index.html" : "/register.html";

        return generateRedirectResponse(redirectLocation);
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

        if(account == null || password == null || email == null) {
            return false;
        }

        if(InMemoryUserRepository.findByAccount(account).isPresent()) {
            return false;
        }
        try{
            final User newUser = new User(account, password, email);
            InMemoryUserRepository.save(newUser);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private String serveStaticFile(final String path) throws IOException, URISyntaxException {
        final byte[] fileBytes = readFile(path);
        return generateOkResponse(path, fileBytes);
    }

    private Map<String, String> parseFormData(final String formData) {
        final Map<String, String> parameters = new HashMap<>();
        if (formData != null && !formData.isEmpty()) {
            final String[] pairs = formData.split("&");
            for (final String pair : pairs) {
                final String[] keyValue = pair.split("=");
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
        return parameters;
    }

    private String generateRedirectResponse(final String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                "Content-Type: text/html; charset=UTF-8 ",
                "Content-Length: 0 ",
                "\r\n");
    }

    private byte[] readFile(final String path) throws IOException, URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource("static" + path);
        final Path filePath = Paths.get(resource.toURI());
        return Files.readAllBytes(filePath);
    }

    private String generateOkResponse(final String path, final byte[] bytes) {
        final String responseBody = new String(bytes);
        final String contentType = getContentType(path);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                "",
                responseBody);
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
