package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final String REQUEST_LINE = "Request-Line";
    private static final String COOKIE = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String TEXT_HTML = "text/html;";
    private static final String TEXT_CSS = "text/css;";
    private static final String INDEX_PAGE = "/index.html";
    private static final String NOT_FOUND_PAGE = "/404.html";
    private static final String HTML_EXTENSION = ".html";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

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
        try (final var inputStreamReader = new InputStreamReader(connection.getInputStream());
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final OutputStream outputStream = connection.getOutputStream()) {
            final Map<String, String> requestHeader = readRequestHeader(bufferedReader);
            final String uri = requestHeader.get(REQUEST_LINE).split(" ")[1];
            final String contentType = requestHeader.get(CONTENT_TYPE);

            final String cookieHeader = requestHeader.getOrDefault(COOKIE, null);
            final var cookie = HttpCookie.from(cookieHeader);

            final String requestBody = readRequestBody(bufferedReader, requestHeader, contentType);
            final String response = handleRequest(uri, requestBody, cookie);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestBody(final BufferedReader bufferedReader, final Map<String, String> requestHeader,
                                   final String contentType) throws IOException {
        if (!Objects.equals(contentType, FORM_CONTENT_TYPE)) {
            return null;
        }
        final var contentLength = Integer.parseInt(requestHeader.get(CONTENT_LENGTH));
        final var buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final var requestBody = new String(buffer);

        log.info("Request-Body: {}", requestBody);
        return requestBody;
    }

    private String handleRequest(final String uri, final String requestBody, final HttpCookie cookie) throws IOException {
        final String path = uri.split("\\?")[0];

        if (path.equals("/")) {
            return get200ResponseMessage(path, "Hello world!");
        }
        if (path.equals("/login")) {
            final String responseBody = findStaticResource(path + HTML_EXTENSION);
            return handleLoginRequest(uri, requestBody, responseBody, cookie);
        }
        if (path.equals("/register")) {
            final String responseBody = findStaticResource(path + HTML_EXTENSION);
            return handleRegisterRequest(uri, requestBody, responseBody, cookie);
        }
        final String responseBody = findStaticResource(path);
        return get200ResponseMessage(path, responseBody);
    }

    private String handleRegisterRequest(final String uri, final String requestBody, final String responseBody,
                                         final HttpCookie cookie) {
        final String[] splitUri = uri.split("\\?");
        if (requestBody == null && splitUri.length == 1) {
            return get200ResponseMessage(splitUri[0], responseBody);
        }
        final Map<String, String> requestBodyValues = getRequestParameters(requestBody, splitUri);
        final var user = new User(requestBodyValues.get("account"), requestBodyValues.get("password"),
                requestBodyValues.get("email"));
        InMemoryUserRepository.save(user);
        return get302ResponseMessage(INDEX_PAGE, cookie, false);
    }

    private Map<String, String> getRequestParameters(final String requestBody, final String[] uri) {
        if (requestBody == null) {
            return parseRequestBody(uri[1]);
        }
        return parseRequestBody(requestBody);
    }

    private Map<String, String> parseRequestBody(final String requestBody) {
        final var requestBodyValues = new HashMap<String, String>();
        final String[] splitRequestBody = requestBody.split("&");
        for (var value : splitRequestBody) {
            final String[] splitValue = value.split("=");
            requestBodyValues.put(splitValue[0], splitValue[1]);
        }
        return requestBodyValues;
    }

    private String handleLoginRequest(final String uri, final String requestBody, final String responseBody,
                                      final HttpCookie cookie) {
        final String[] splitUri = uri.split("\\?");
        final User user = findUserBySessionId(cookie.getJSessionId(false));
        if (user != null) {
            log.info("User: {}", user);
            return get302ResponseMessage(INDEX_PAGE, cookie, false);
        }
        if (requestBody == null && splitUri.length == 1) {
            return get200ResponseMessage(splitUri[0], responseBody);
        }
        return handleFirstLogin(requestBody, cookie, splitUri);
    }

    private String handleFirstLogin(final String requestBody, final HttpCookie cookie, final String[] uri) {
        final Map<String, String> requestBodyValues = getRequestParameters(requestBody, uri);
        final Optional<User> user = InMemoryUserRepository.findByAccount(requestBodyValues.get("account"));
        if (user.isEmpty() || !user.get().checkPassword(requestBodyValues.get("password"))) {
            return get302ResponseMessage("/401.html", cookie, false);
        }
        addSession(cookie, user.get());
        log.info("User: {}", user.get());
        return get302ResponseMessage(INDEX_PAGE, cookie, true);
    }

    private void addSession(final HttpCookie cookie, final User user) {
        final var session = new Session(cookie.getJSessionId(true));
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);
    }

    private User findUserBySessionId(final String sessionId) {
        if (sessionId == null) {
            return null;
        }
        final Session session = SESSION_MANAGER.findSession(sessionId);
        return (User) session.getAttribute("user");
    }

    private String get200ResponseMessage(final String path, final String responseBody) {
        final String contentType = getContentType(path);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + "charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return TEXT_CSS;
        }
        return TEXT_HTML;
    }

    private String get302ResponseMessage(final String location, final HttpCookie cookie, final boolean setCookie) {
        if (setCookie) {
            return String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: " + location + " ",
                    "Set-Cookie: JSESSIONID=" + cookie.getJSessionId(false));
        }
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location);
    }

    private String findStaticResource(final String uri) throws IOException {
        final URL fileUrl = findResourceUrl(uri);
        final String filePath = fileUrl.getPath();
        return Files.readString(new File(filePath).toPath());
    }

    private URL findResourceUrl(final String uri) {
        final URL fileUrl = getClass().getClassLoader().getResource("./static" + uri);
        if (fileUrl == null) {
            return getClass().getClassLoader().getResource("./static" + NOT_FOUND_PAGE);
        }
        return fileUrl;
    }

    private Map<String, String> readRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final var requestHeader = new HashMap<String, String>();

        String line = bufferedReader.readLine();
        if (line == null) {
            return Map.of();
        }
        requestHeader.put(REQUEST_LINE, line);

        while (!"".equals(line = bufferedReader.readLine())) {
            final String[] splitLine = line.split(": ");
            requestHeader.put(splitLine[0], splitLine[1]);
        }
        return requestHeader;
    }
}
