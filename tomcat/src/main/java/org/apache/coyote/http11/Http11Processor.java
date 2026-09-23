package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String HTTP_VERSION = "HTTP/1.1";

    public static final String CONTENT_TYPE_HEADER = "Content-Type:";
    public static final String CHARSET_UTF_8 = "charset=utf-8";
    public static final String CONTENT_LENGTH = "Content-Length:";

    public static final String HOME_PATH = "/";
    public static final String LOGIN_PATH = "/login";
    public static final String CRLF = "\r\n";
    public static final String HTML_EXTENSION = ".html";
    public static final String REGISTER_PATH = "/register";

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

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String requestHead = getRequestMessage(bufferedReader);
            String[] requestHeadLines = requestHead.split(CRLF);

            String[] requestLineParts = requestHeadLines[0].split(" ");
            String httpMethod = requestLineParts[0];
            String requestUri = requestLineParts[1];

            String location = null;
            if (requestUri.contains("?")) {
                String[] uriParts = requestUri.split("\\?", 2);
                requestUri = uriParts[0];
            }

            if ("POST".equals(httpMethod)) {
                Integer contentLength = getContentLength(requestHeadLines);

                char[] buffer = new char[Objects.requireNonNull(contentLength)];
                bufferedReader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);

                Map<String, String> parameters = parseFormParameters(requestBody);

                if (requestUri.equals(REGISTER_PATH)) {
                    User user = new User(parameters.get("account"), parameters.get("password"),
                            parameters.get("email"));

                    InMemoryUserRepository.save(user);
                    location = "/index.html";
                }

                if (requestUri.equals(LOGIN_PATH)) {
                    String account = parameters.get("account");
                    String password = parameters.get("password");

                    location = resolveLoginRedirectLocation(account, password);
                }
            }

            final String responseBody = getResponseBody(requestUri);
            if (responseBody == null) {
                return;
            }

            sendResponse(requestUri, location, responseBody, outputStream);

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String resolveLoginRedirectLocation(String account, String password) {
        if (authenticate(account, password)) {
            return "/index.html";
        }
        return "/401.html";
    }

    private Integer getContentLength(String[] requestHeadLines) {
        for (String requestHeadLine : requestHeadLines) {
            if (requestHeadLine.contains("Content-Length")) {
                return Integer.parseInt(requestHeadLine.split(" ")[1]);
            }
        }
        return null;
    }

    private String getRequestMessage(BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();

        String line = bufferedReader.readLine();
        while (line != null && !line.isBlank()) {
            stringBuilder.append(line).append(CRLF);
            line = bufferedReader.readLine();
        }

        return stringBuilder.toString();
    }

    private void sendResponse(String requestUri, String location, String responseBody, OutputStream outputStream)
            throws IOException {
        String contentType = getContentType(requestUri);
        final var response = getResponse(contentType, location, responseBody);

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String getResponse(String contentType, String location, String responseBody) {
        if (location != null && !location.isBlank()) {
            return String.join(CRLF,
                    HTTP_VERSION + " " + "302 FOUND" + " ",
                    "Location:" + location,
                    contentType,
                    CONTENT_LENGTH + " " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);
        }

        return String.join(CRLF,
                HTTP_VERSION + " " + "200 OK" + " ",
                contentType,
                CONTENT_LENGTH + " " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
    }

    private Map<String, String> parseFormParameters(String queryString) {
        Map<String, String> encodedParameters = new HashMap<>();

        String[] queryPairs = queryString.split("&");
        for (String queryPair : queryPairs) {
            String[] keyAndValue = queryPair.split("=");
            encodedParameters.put(keyAndValue[0], keyAndValue[1]);
        }
        return encodedParameters;
    }

    private boolean authenticate(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return false;
        }

        User foundUser = user.get();
        if (foundUser.checkPassword(password)) {
            log.info("로그인 성공! 아이디 : {}", foundUser.getAccount());
            return true;
        }
        return false;
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return CONTENT_TYPE_HEADER + " " + "text/css;" + CHARSET_UTF_8 + " ";
        }
        return CONTENT_TYPE_HEADER + " " + "text/html;" + CHARSET_UTF_8 + " ";
    }

    private String getResponseBody(String requestUri) throws URISyntaxException, IOException {
        if (Objects.equals(requestUri, HOME_PATH)) {
            return "Hello world!";
        }

        if (requestUri.contains(LOGIN_PATH)) {
            requestUri = LOGIN_PATH + HTML_EXTENSION;
        }

        if (requestUri.equals(REGISTER_PATH)) {
            requestUri = REGISTER_PATH + HTML_EXTENSION;
        }

        final URL resource = getClass().getClassLoader().getResource("static" + requestUri);
        if (resource == null) {
            log.warn("존재하지 않는 경로 : {}", requestUri);
            return null;
        }

        final Path path = Paths.get(resource.toURI());

        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }
}
