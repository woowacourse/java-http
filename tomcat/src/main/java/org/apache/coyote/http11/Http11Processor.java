package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.session.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager sessionManager = SessionManager.getInstance();

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

            final RequestLine requestLine = RequestLine.parse(readLine(inputStream));
            if (requestLine == null) return;
            final HttpHeaders requestHeaders = HttpHeaders.from(readHeaders(inputStream));
            final byte[] messageBody = inputStream.readNBytes(requestHeaders.getContentLength());

            final HttpRequest request = new HttpRequest(requestLine, requestHeaders, messageBody);

            final HttpHeaders responseHeaders  = new HttpHeaders(new HashMap<>());
            Session session;
            final HttpCookie httpCookie = HttpCookie.from(requestHeaders.get("Cookie"));
            if (httpCookie.contains("JSESSIONID")) {
                final String sessionId = httpCookie.get("JSESSIONID");
                session = sessionManager.findSession(sessionId);
            } else {
                final String sessionId = String.valueOf(UUID.randomUUID());
                session = new Session(sessionId);
                sessionManager.add(session);
                responseHeaders.add("Set-Cookie", "JSESSIONID=" + sessionId);
            }

            final HttpResponse response = handleRequest(request, responseHeaders, session);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readLine(final InputStream inputStream) throws IOException {
        try (final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int current;
            while ((current = inputStream.read()) != -1) {
                if (current == '\r') {
                    final int next = inputStream.read();

                    if (next == '\n') {
                        break;
                    }

                    buffer.write(current);

                    if (next != -1) {
                        buffer.write(next);
                    }

                    continue;
                }

                buffer.write(current);
            }

            return buffer.toString();
        }
    }

    private List<String> readHeaders(final InputStream reader) throws IOException {
        final List<String> headers = new ArrayList<>();
        String line;
        while  (!(line = readLine(reader)).isBlank()) {
            headers.add(line);
        }
        return headers;
    }

    private HttpResponse handleRequest(final HttpRequest httpRequest, final HttpHeaders responseHeaders, final Session session) throws IOException {
        final RequestLine requestLine = httpRequest.requestLine();
        final String messageBody = new String(httpRequest.body());

        if (requestLine.isGetMethod()) {
            return handleGetRequest(requestLine, responseHeaders, session);
        }

        if (requestLine.isPostMethod()) {
            return handlePostRequest(requestLine, messageBody, responseHeaders, session);
        }

        return HttpResponse.createForwardResponse(
                new StatusLine(requestLine.protocolVersion(), HttpStatusCode.NOT_FOUND),
                "/404.html",
                responseHeaders);
    }

    private HttpResponse handleGetRequest(final RequestLine requestLine, final HttpHeaders responseHeaders, final Session session) throws IOException {
        final String requestURI = requestLine.path();
        final String protocolVersion = requestLine.protocolVersion();

        if (requestURI.equals("/")) {
            return HttpResponse.createForwardResponse(
                    new StatusLine(protocolVersion, HttpStatusCode.OK),
                    "/index.html",
                    responseHeaders
            );
        }

        if (requestURI.equals("/login") || requestURI.equals("/login.html")) {
            if (session.getAttribute("user") != null) {
                return HttpResponse.createRedirectResponse(
                        protocolVersion,
                        "/index.html",
                        responseHeaders
                );
            }
            return HttpResponse.createForwardResponse(
                    new StatusLine(protocolVersion, HttpStatusCode.OK),
                    "/login.html",
                    responseHeaders
            );
        }

        if (requestURI.equals("/register")) {
            return HttpResponse.createForwardResponse(
                    new StatusLine(protocolVersion, HttpStatusCode.OK),
                    "/register.html",
                    responseHeaders);
        }

        return HttpResponse.createForwardResponse(
                new StatusLine(protocolVersion, HttpStatusCode.OK),
                requestURI,
                responseHeaders);
    }

    private HttpResponse handlePostRequest(final RequestLine requestLine, final String messageBody, final HttpHeaders responseHeaders, final Session session) throws IOException {
        final String requestURI = requestLine.path();
        final String protocolVersion = requestLine.protocolVersion();

        if (requestURI.equals("/login")) {
            final boolean hasLoginSucceeded = loginAndRetrieveUserInfo(messageBody, session);
            if (hasLoginSucceeded) {
                return HttpResponse.createRedirectResponse(
                        protocolVersion,
                        "/index.html",
                        responseHeaders
                );
            }
            return HttpResponse.createRedirectResponse(
                    protocolVersion,
                    "/401.html",
                    responseHeaders
            );
        }

        if (requestURI.equals("/register")) {
            final boolean isRegistered  = registerNewUser(messageBody);
            if (isRegistered) {
                return HttpResponse.createRedirectResponse(
                        protocolVersion,
                        "/index.html",
                        responseHeaders
                );
            }
            return HttpResponse.createForwardResponse(
                    new StatusLine(protocolVersion, HttpStatusCode.BAD_REQUEST),
                    "/register.html",
                    responseHeaders);
        }

        return HttpResponse.createForwardResponse(
                new StatusLine(protocolVersion, HttpStatusCode.NOT_FOUND),
                "/404.html",
                responseHeaders);
    }

    private boolean loginAndRetrieveUserInfo(final String messageBody, final Session session) {
        final Map<String, String> loginInfoPairs = parseQuery(messageBody);
        String account = loginInfoPairs.getOrDefault("account", "");
        String password = loginInfoPairs.getOrDefault("password", "");

        if (!account.isBlank() && !password.isBlank()) {
            Optional<User> retrieveResult = InMemoryUserRepository.findByAccount(account);
            if (retrieveResult.isEmpty()) {
                return false;
            }
            final User retrievedUser = retrieveResult.get();
            if (retrievedUser.checkPassword(password)) {
                session.setAttribute("user", retrievedUser);
                log.info("로그인 성공! 아이디 : {}", retrievedUser.getAccount());
                log.info("User : {}", retrievedUser);
                return true;
            }
        }

        return false;
    }

    private Map<String, String> parseQuery(final String queryString)  {
        final Map<String, String> queryPairs = new HashMap<>();
        for (String queryPair : queryString.split("&")) {
            final int splitIndex = queryPair.indexOf("=");
            final String key = queryPair.substring(0, splitIndex).trim();
            final String value = queryPair.substring(splitIndex + 1).trim();
            queryPairs.put(key, value);
        }
        return queryPairs;
    }

    private boolean registerNewUser(final String messageBody) {
        final Map<String, String> registerInfoPairs = parseQuery(messageBody);
        String account = registerInfoPairs.getOrDefault("account", "");
        String password = registerInfoPairs.getOrDefault("password", "");
        String email = registerInfoPairs.getOrDefault("email", "");

        if (!account.isBlank() && !password.isBlank() && !email.isBlank()) {
            final User newUser = new User(account, password, email);
            InMemoryUserRepository.save(newUser);
            return true;
        }

        return false;
    }
}
