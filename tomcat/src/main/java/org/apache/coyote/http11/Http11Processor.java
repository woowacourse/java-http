package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMITER = "=";
    private static final String JSESSION_ID_KEY = "JSESSIONID";

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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            final String requestLine = bufferedReader.readLine();
            final Map<String, String> httpRequestHeaders = readHttpRequestHeaders(bufferedReader);

            final HttpCookie httpCookie = new HttpCookie(httpRequestHeaders.get("Cookie"));

            final String requestBody = readRequestBody(bufferedReader, httpRequestHeaders);

            final String requestTarget = requestLine.split(" ")[1];
            final URI uri = URI.create(requestTarget);
            log.info("request uri: {}", uri);

            final HttpResponse response = handleRequest(uri, requestTarget, httpCookie, requestBody);

            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHttpRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> httpRequestHeaders = new HashMap<>();
        String line;

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] headers = line.split(": ");
            httpRequestHeaders.put(headers[0], headers[1]);
        }

        return httpRequestHeaders;
    }

    private String readRequestBody(final BufferedReader bufferedReader, final Map<String, String> httpRequestHeaders) throws IOException {
        String requestBody = null;
        if (httpRequestHeaders.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            requestBody = new String(buffer);
        }

        return requestBody;
    }

    private HttpResponse handleRequest(final URI uri, final String requestTarget, final HttpCookie httpCookie, final String requestBody) throws IOException {
        final String uriPath = uri.getPath();

        if (uriPath.equals("/login")) {
            return handleLogin(uri, requestTarget, httpCookie, requestBody);
        }

        if (uriPath.equals("/register")) {
            return handleRegister(requestTarget, requestBody);
        }

        return createFileResponse(getFilePath(uriPath), requestTarget, "200 OK");
    }

    private HttpResponse handleLogin(final URI uri, final String requestTarget, final HttpCookie httpCookie, final String requestBody) throws IOException {
        final String uriPath = uri.getPath();
        Path filePath = getFilePath(uriPath);
        String httpStatus;
        String location = null;
        String jsessionId = httpCookie.get(JSESSION_ID_KEY);

        final String query = uri.getQuery();
        boolean loginSuccess = false;
        if (query != null) {
            loginSuccess = login(extractQueryParams(query));
        }
        if (requestBody != null) {
            loginSuccess = login(extractQueryParams(requestBody));
        }

        if (loginSuccess) {
            httpStatus = "302 Found";
            location = "/index.html";
            if (jsessionId == null) {
                httpCookie.put(JSESSION_ID_KEY, UUID.randomUUID().toString());
            }
        }
        else if (query == null && requestBody == null){
            httpStatus = "200 OK";
            filePath = resolveResourcePath("static/login.html");
        }
        else {
            httpStatus = "401 Unauthorized";
            filePath = resolveResourcePath("static/401.html");
        }

        final String contentType = getContentType(requestTarget);
        final String responseBody = location == null ? getResponseBody(filePath) : "";
        return new HttpResponse(httpStatus, contentType, responseBody, location, httpCookie);
    }

    private HttpResponse handleRegister(final String requestTarget, final String requestBody) throws IOException {
        if (requestBody != null) {
            createUser(extractQueryParams(requestBody));
        }

        return createFileResponse(resolveResourcePath("static/register.html"), requestTarget, "200 OK");
    }

    private HttpResponse createFileResponse(final Path filePath, final String requestTarget, final String httpStatus) throws IOException {
        final String contentType = getContentType(requestTarget);
        final String responseBody = getResponseBody(filePath);
        return new HttpResponse(httpStatus, contentType, responseBody, null, null);
    }

    private Path getFilePath(final String uriPath) {
        if (uriPath.equals("/")) {
            return Path.of("/");
        }
        if (uriPath.equals("/login")) {
            return resolveResourcePath("static/login.html");
        }
        else {
            return resolveResourcePath("static" + uriPath);
        }
    }

    private Map<String, String> extractQueryParams(final String query) {
        final String[] queryParams = query.split(QUERY_PARAM_DELIMITER);

        final Map<String, String> params = new HashMap<>();

        for (String queryParam : queryParams) {
            String[] pair = queryParam.split(QUERY_PARAM_VALUE_DELIMITER, 2);
            String key = pair[0];
            String value = pair.length == 2 ? pair[1] : "";

            params.put(key, value);
        }
        return params;
    }

    private boolean login(final Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            log.error("login error");
            return false;
        }

        log.info("user : {}", user.get());
        return true;
    }

    private void createUser(final Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        User byAccount = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        log.info("byAccount = {}", byAccount);
    }

    private String getResponseBody(final Path filePath) throws IOException {
        if (filePath.equals(Path.of("/"))) {
            return "Hello world!";
        }

        return Files.readString(filePath);
    }

    private String getContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        if (requestUri.endsWith(".js")) {
            return "text/javascript;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }

    private Path resolveResourcePath(final String name) {
        final URL url = getClass().getClassLoader().getResource(name);
        if (url == null)
            return Path.of("/");
        return Path.of(url.getPath());
    }

    private void writeResponse(final OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(createResponse(response).getBytes());
        outputStream.flush();
    }

    private String createResponse(final HttpResponse response) {
        log.info("response status: {}", response.status());
        StringBuilder httpResponse = new StringBuilder()
                .append("HTTP/1.1 ").append(response.status()).append(" ").append("\r\n")
                .append("Content-Type: ").append(response.contentType()).append(" ").append("\r\n")
                .append("Content-Length: ").append(response.body().getBytes().length).append(" ").append("\r\n");

        if (response.location() != null) {
            httpResponse.append("Location: ").append(response.location()).append(" ").append("\r\n");
        }
        if (response.cookie() != null && response.cookie().contains(JSESSION_ID_KEY)) {
            httpResponse.append("Set-Cookie: ")
                    .append(JSESSION_ID_KEY)
                    .append("=")
                    .append(response.cookie().get(JSESSION_ID_KEY))
                    .append(" ").append("\r\n");
        }

        return httpResponse.append("\r\n")
                .append(response.body())
                .toString();
    }

    private record HttpResponse(String status, String contentType, String body, String location, HttpCookie cookie) { }
}
