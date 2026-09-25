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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String HTTP_VERSION = "HTTP/1.1";

    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CHARSET_UTF_8 = "charset=utf-8";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String SET_COOKIE = "Set-Cookie";
    private static final String LOCATION = "Location";
    public static final String COOKIE = "Cookie";

    public static final String HOME_PATH = "/";
    public static final String LOGIN_PATH = "/login";
    public static final String CRLF = "\r\n";
    public static final String HTML_EXTENSION = ".html";
    public static final String REGISTER_PATH = "/register";
    public static final String INDEX_HTML = "/index.html";

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection, final SessionManager sessionManager) {
        this.connection = connection;
        this.sessionManager = sessionManager;
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

            final BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String requestHead = getRequestMessage(bufferedReader);
            String[] requestHeadLines = requestHead.split(CRLF);

            String[] requestLineParts = requestHeadLines[0].split(" ");
            String httpMethod = requestLineParts[0];
            String requestUri = requestLineParts[1];
            String cookieLine = getCookieLine(requestHeadLines);

            Map<String, String> responseHeaders = new LinkedHashMap<>();

            HttpCookie cookie = HttpCookie.parse(cookieLine);
            Session session = getOrCreateJSessionId(cookie, responseHeaders);

            if (requestUri.contains("?")) {
                String[] uriParts = requestUri.split("\\?", 2);
                requestUri = uriParts[0];
            }

            if ("POST".equals(httpMethod)) {
                Integer contentLength = getContentLength(requestHeadLines);

                char[] buffer = new char[Objects.requireNonNull(contentLength)];
                int readCount = bufferedReader.read(buffer, 0, contentLength);

                if (readCount == -1) {
                    throw new IOException("요청 본문을 읽지 못했습니다.");
                }
                String requestBody = new String(buffer, 0, readCount);

                Map<String, String> parameters = parseFormParameters(requestBody);

                if (requestUri.equals(REGISTER_PATH)) {
                    User user = new User(parameters.get("account"), parameters.get("password"),
                            parameters.get("email"));

                    InMemoryUserRepository.save(user);
                    responseHeaders.put(LOCATION, INDEX_HTML);
                }

                if (requestUri.equals(LOGIN_PATH)) {
                    String account = parameters.get("account");
                    String password = parameters.get("password");

                    Optional<User> authenticatedUser = authenticate(account, password);
                    if (authenticatedUser.isPresent()) {
                        User user = authenticatedUser.get();
                        session.setAttribute("user", user);

                        log.info("로그인 성공! 아이디 : {}", user.getAccount());
                        responseHeaders.put(LOCATION, INDEX_HTML);
                    } else {
                        responseHeaders.put(LOCATION, "/401.html");
                    }

                }
            }

            if ("GET".equals(httpMethod) && LOGIN_PATH.equals(requestUri)) {
                User user = (User) session.getAttribute("user");
                if (user != null) {
                    responseHeaders.put(LOCATION, INDEX_HTML);
                }
            }

            final String responseBody = getResponseBody(requestUri);
            if (responseBody == null) {
                return;
            }

            sendResponse(requestUri, responseHeaders, responseBody, outputStream);

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Session getOrCreateJSessionId(HttpCookie cookie, Map<String, String> headers) throws IOException {
        if (!cookie.contains("JSESSIONID")) {
            return createSession(headers);
        }

        String jSessionId = cookie.getJSessionId();
        Session session = sessionManager.findSession(jSessionId);

        if (session == null) {
            return createSession(headers);
        }

        return session;
    }

    private Session createSession(Map<String, String> headers) {
        Session session;
        String sessionId = UUID.randomUUID().toString();
        headers.put(SET_COOKIE, "JSESSIONID=" + sessionId + ";");

        session = new Session(sessionId);
        sessionManager.add(session);
        return session;
    }

    private String getCookieLine(String[] requestHeadLines) {
        for (String requestHeadLine : requestHeadLines) {
            if (requestHeadLine.startsWith(COOKIE + ":")) {
                return requestHeadLine;
            }
        }
        return null;
    }

    private Integer getContentLength(String[] requestHeadLines) {
        for (String requestHeadLine : requestHeadLines) {
            if (requestHeadLine.contains(CONTENT_LENGTH + ":")) {
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

    private void sendResponse(String requestUri, Map<String, String> headers, String responseBody,
                              OutputStream outputStream)
            throws IOException {
        String contentType = getContentType(requestUri);

        final var response = getResponse(contentType, headers, responseBody);

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String getResponse(String contentType, Map<String, String> headers, String responseBody) {
        String httpStatus = "200 OK";
        if (headers.containsKey(LOCATION)) {
            httpStatus = "302 FOUND";
        }

        StringBuilder response = new StringBuilder();
        response.append(HTTP_VERSION).append(" ").append(httpStatus).append(" ").append(CRLF);

        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(CRLF);
        }

        response.append(contentType).append(CRLF);
        response.append(CONTENT_LENGTH).append(": ")
                .append(responseBody.getBytes(StandardCharsets.UTF_8).length)
                .append(" ")
                .append(CRLF);
        response.append(CRLF);
        response.append(responseBody);

        return response.toString();
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

    private Optional<User> authenticate(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return CONTENT_TYPE_HEADER + ": " + "text/css;" + CHARSET_UTF_8 + " ";
        }
        return CONTENT_TYPE_HEADER + ": " + "text/html;" + CHARSET_UTF_8 + " ";
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
