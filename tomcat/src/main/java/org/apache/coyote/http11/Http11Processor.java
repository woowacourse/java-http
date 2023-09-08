package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.http11.response.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final String TEXT_HTML = "text/html;charset=utf-8";
    private static final String TEXT_CSS = "text/css;";
    private static final String INDEX_PAGE = "/index.html";
    private static final String NOT_FOUND_PAGE = "/404.html";
    private static final String HTML_EXTENSION = ".html";
    private static final String COOKIE = "Cookie";

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
            final var httpRequest = HttpRequest.from(bufferedReader);

            final String response = handleRequest(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String handleRequest(final HttpRequest httpRequest) throws IOException {
        final String uri = httpRequest.getRequestLine().getPath();
        final String path = uri.split("\\?")[0];

        if (path.equals("/")) {
            return get200ResponseMessage(httpRequest, path, "Hello world!");
        }
        if (path.equals("/login")) {
            final String responseBody = findStaticResource(path + HTML_EXTENSION);
            return handleLoginRequest(httpRequest, uri, responseBody);
        }
        if (path.equals("/register")) {
            final String responseBody = findStaticResource(path + HTML_EXTENSION);
            return handleRegisterRequest(httpRequest, uri, responseBody);
        }
        final String responseBody = findStaticResource(path);
        return get200ResponseMessage(httpRequest, path, responseBody);
    }

    private String handleRegisterRequest(final HttpRequest httpRequest, final String uri, final String responseBody) {
        final String[] splitUri = uri.split("\\?");
        final String requestBody = httpRequest.getRequestBody();
        if (requestBody == null && splitUri.length == 1) {
            return get200ResponseMessage(httpRequest, splitUri[0], responseBody);
        }
        final Map<String, String> requestBodyValues = getRequestParameters(requestBody, splitUri);
        final var user = new User(requestBodyValues.get("account"), requestBodyValues.get("password"),
                requestBodyValues.get("email"));
        InMemoryUserRepository.save(user);
        return get302ResponseMessage(httpRequest, INDEX_PAGE, null);
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

    private String handleLoginRequest(final HttpRequest httpRequest, final String uri, final String responseBody) {
        final String cookieHeader = httpRequest.getRequestHeader().getValue(COOKIE);
        final var cookie = HttpCookie.from(cookieHeader);
        final String[] splitUri = uri.split("\\?");
        final String requestBody = httpRequest.getRequestBody();
        final User user = findUserBySessionId(cookie.getJSessionId());
        if (user != null) {
            log.info("User: {}", user);
            return get302ResponseMessage(httpRequest, INDEX_PAGE, null);
        }
        if (requestBody == null && splitUri.length == 1) {
            return get200ResponseMessage(httpRequest, splitUri[0], responseBody);
        }
        return handleFirstLogin(httpRequest, requestBody, splitUri);
    }

    private String handleFirstLogin(final HttpRequest httpRequest, final String requestBody, final String[] uri) {
        final Map<String, String> requestBodyValues = getRequestParameters(requestBody, uri);
        final Optional<User> user = InMemoryUserRepository.findByAccount(requestBodyValues.get("account"));
        if (user.isEmpty() || !user.get().checkPassword(requestBodyValues.get("password"))) {
            return get302ResponseMessage(httpRequest, "/401.html", null);
        }
        final String sessionId = addSession(user.get());
        log.info("User: {}", user.get());
        return get302ResponseMessage(httpRequest, INDEX_PAGE, sessionId);
    }

    private String addSession(final User user) {
        final var session = Session.create();
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);
        return session.getId();
    }

    private User findUserBySessionId(final String sessionId) {
        if (sessionId == null) {
            return null;
        }
        final Session session = SESSION_MANAGER.findSession(sessionId)
                .orElseGet(Session::create);
        return (User) session.getAttribute("user");
    }

    private String get200ResponseMessage(final HttpRequest httpRequest, final String path, final String responseBody) {
        final var statusLine = StatusLine.of(httpRequest.getRequestLine().getProtocol(), HttpStatus.OK);
        final var responseHeader = ResponseHeader.createEmpty();
        responseHeader.addHeader("Content-Type", getContentType(path));
        responseHeader.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));

        final var httpResponse = HttpResponse.createGetResponse(statusLine, responseHeader, responseBody);
        return httpResponse.toString();
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return TEXT_CSS;
        }
        return TEXT_HTML;
    }

    private String get302ResponseMessage(final HttpRequest httpRequest, final String location, final String newSessionId) {
        final var statusLine = StatusLine.of(httpRequest.getRequestLine().getProtocol(), HttpStatus.FOUND);
        final var responseHeader = ResponseHeader.createEmpty();
        responseHeader.addHeader("Location", location);
        if (newSessionId != null) {
            responseHeader.addHeader("Set-Cookie", "JSESSIONID=" + newSessionId);
        }
        final var httpResponse = HttpResponse.createPostResponse(statusLine, responseHeader);
        return httpResponse.toString();
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
}
