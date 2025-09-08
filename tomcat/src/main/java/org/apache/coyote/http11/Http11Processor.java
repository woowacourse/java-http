package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager sessionManager = new SessionManager();

    private final Socket connection;

    private String method;
    private String path;
    private Map<String, String> queryString;
    private String httpVersion;
    private Map<String, String> httpRequestHeaders;
    private Map<String, String> requestBody;
    private HttpCookie httpCookie;

    private String responseStatus;
    private Map<String, String> responseHeaders = new LinkedHashMap<>();
    private String responseBody;
    private List<String> responseCookies = new ArrayList<>();

    private static final String STATUS_OK = "HTTP/1.1 200 OK ";
    private static final String STATUS_FOUND = "HTTP/1.1 302 Found ";
    private static final String STATUS_UNAUTHORIZED = "HTTP/1.1 401 Unauthorized ";

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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            readRequest(bufferedReader);
            route();
            writeResponse(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void readRequest(final BufferedReader bufferedReader) throws IOException {
        readRequestHeader(bufferedReader);
        readRequestBody(bufferedReader);
        readHttpCookie();
    }

    private void route() throws IOException, URISyntaxException {
        if ("GET".equals(method)) {
            handleGetMethod();
        }
        if("POST".equals(method)) {
            handlePostMethod();
        }
    }

    private void writeResponse(final OutputStream outputStream) throws IOException {
        String response = buildResponse();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void readHttpCookie() {
        String cookieHeader = httpRequestHeaders.get("Cookie");
        httpCookie = new HttpCookie();

        if (cookieHeader == null || cookieHeader.isBlank()) {
            return;
        }

        Arrays.stream(cookieHeader.split(";"))
                .map(String::trim)
                .map(kv -> kv.split("=", 2))
                .filter(kv -> kv.length == 2)
                .forEach(kv -> httpCookie.add(kv[0], kv[1]));
    }

    private void readRequestHeader(final BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        if (requestLine == null) {
            return;
        }
        String[] parts = requestLine.split(" ");
        method = parts[0];

        String uri = parts[1];
        path = getPathByUri(uri);
        queryString = getQueryStringByUri(uri);

        httpRequestHeaders = getHttpRequestHeaders(bufferedReader);
    }

    private void readRequestBody(final BufferedReader bufferedReader) throws IOException {
        String contentLengthHeader = httpRequestHeaders.get("Content-Length");
        if (contentLengthHeader == null) {
            requestBody = new HashMap<>();
            return;
        }
        int contentLength = Integer.parseInt(contentLengthHeader.trim());

        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBodyEncoded = new String(buffer);
        requestBody = parseUrlEncoded(requestBodyEncoded);
    }

    private Map<String, String> getHttpRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> httpRequestHeaders = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            int idx = line.indexOf(":");
            if (idx > 0) {
                String name = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                httpRequestHeaders.put(name, value);
            }
        }
        return httpRequestHeaders;
    }

    private void handleGetMethod() throws IOException, URISyntaxException {
        if ("/".equals(path)) {
            okResponse("Hello world!", "text/html;charset=utf-8");
            return;
        }
        if ("/login".equals(path)) {
            okResponse("/login.html");
            return;
        }
        if("/register".equals(path)) {
            okResponse("/register.html");
            return;
        }
        okResponse(path);
    }

    private void handlePostMethod() throws IOException, URISyntaxException {
        if("/register".equals(path)) {
            handleRegister();
            return;
        }
        if("/login".equals(path)) {
            handleLogin();
            return;
        }
        foundResponse("/404.html");
    }

    private void handleRegister() {
        User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);
        foundResponse("/index.html");
    }

    private void handleLogin() throws IOException, URISyntaxException {
        if(requestBody.isEmpty()) {
            log.info("login failed: queryString is empty");
            foundResponse("/401.html");
            return;
        }
        if(isLoggedIn()) {
            log.info("login successful: already logged in");
            okResponse("/index.html");
            return;
        }
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(requestBody.get("account"));
        if(userOptional.isPresent() && userOptional.get().checkPassword(requestBody.get("password"))) {
            loginInSession(userOptional.get());
            return;
        }
        log.info("login failed: invalid user info");
        foundResponse("/401.html");
    }

    private void loginInSession(final User user) {
        log.info("User{}", user);
        Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        session.setAttribute("user", user);
        responseCookies.add("JSESSIONID=" + session.getId());
        foundResponse("/index.html");
        log.info("login successful: logged in");
        return;
    }

    private String getPathByUri(final String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private Map<String, String> getQueryStringByUri(final String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return Map.of();
        }
        String queryString = uri.substring(index + 1);
        return getQueryString(queryString);
    }

    private Map<String, String> getQueryString(final String querystring) {
        return parseUrlEncoded(querystring);
    }

    private static Map<String, String> parseUrlEncoded(final String querystring) {
        return Arrays.stream(querystring.split("&"))
                .map(param -> param.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> urlDecode(parts[0]),
                        parts -> urlDecode(parts[1]))
                );
    }

    private boolean isLoggedIn() {
        if(httpCookie.hasNoSession()) {
            return false;
        }
        Session session = sessionManager.findSession(httpCookie.getSessionId());
        if(session == null) {
            return false;
        }
        User user = getUser(session);
        return user != null && InMemoryUserRepository.findByAccount(user.getAccount()).isPresent();
    }

    private User getUser(final Session session) {
        return (User) session.getAttribute("user");
    }

    private void foundResponse(final String redirectLocation) {
        responseStatus = STATUS_FOUND;

        responseHeaders.put("Location", redirectLocation);
        responseHeaders.put("Content-Length", "0");
    }


    private void okResponse(String path) throws IOException, URISyntaxException {
        URL url = getClass().getClassLoader().getResource("static" + path);
        if(url == null) {
            foundResponse("/404.html");
            return;
        }
        responseBody = new String(Files.readAllBytes(Paths.get(url.toURI())));
        responseStatus = STATUS_OK;

        responseHeaders.put("Content-Type", getContentType(path));
        responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
    }

    private void okResponse(String body, String contentType) {
        responseBody = body;
        responseStatus = STATUS_OK;
        responseHeaders.put("Content-Type", contentType);
        responseHeaders.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    private String buildResponse() {
        StringBuilder builder = new StringBuilder();
        builder.append(responseStatus).append("\r\n");

        for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append(" ").append("\r\n");
        }

        for (String cookie : responseCookies) {
            builder.append("Set-Cookie: ").append(cookie).append(" ").append("\r\n");
        }
        builder.append("\r\n");

        if (responseBody != null) {
            builder.append(responseBody);
        }
        return builder.toString();
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private static String urlDecode(String value) {
        try {
            return java.net.URLDecoder.decode(value, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }
}
