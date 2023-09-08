package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.apache.coyote.http11.request.HttpMethod.POST;
import static org.apache.coyote.http11.response.HttpContentType.TEXT_HTML;
import static org.apache.coyote.http11.response.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.response.HttpHeader.LOCATION;
import static org.apache.coyote.http11.response.HttpHeader.SET_COOKIE;
import static org.apache.coyote.http11.response.ResponseStatus.FOUND;
import static org.apache.coyote.http11.response.ResponseStatus.OK;
import static org.apache.coyote.http11.response.ResponseStatus.UNAUTHORIZED;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_BODY = "Hello world!";
    private static final String HTTP_VERSION = "HTTP/1.1";

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection, SessionManager sessionManager) {
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
        try (
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), UTF_8)
                );
                final OutputStream outputStream = connection.getOutputStream()
        ) {
            HttpRequest httpRequest = readHttpRequest(reader);
            HttpResponse httpResponse = new HttpResponse(HTTP_VERSION);

            String response = process(httpRequest, httpResponse);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(BufferedReader reader) throws IOException {
        String requestLine = HttpRequestReader.readLine(reader);
        String requestHeader = HttpRequestReader.readHeader(reader);
        HttpRequest request = HttpRequest.of(requestLine, requestHeader);

        request.setRequestBody(HttpRequestReader.readBody(reader, request.contentLength()));
        return request;
    }

    private String process(HttpRequest request, HttpResponse response) {
        // 특별한 친구
        if (request.consistsOf(GET, "/")) {
            response.setResponseStatus(OK);
            response.setResponseBody(DEFAULT_BODY);

            response.setResponseHeader(CONTENT_TYPE, TEXT_HTML.mimeTypeWithCharset(UTF_8));
            response.setResponseHeader(CONTENT_LENGTH, String.valueOf(response.measureContentLength()));
            return response.responseMessage();
        }

        // login
        if (request.consistsOf(POST, "/login", "/login.html")) {
            String account = request.getBodyValue("account");
            String password = request.getBodyValue("password");

            Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (user.checkPassword(password)) {
                    Session session = new Session(UUID.randomUUID().toString());
                    sessionManager.add(session);

                    response.setResponseStatus(FOUND);
                    response.setResponseHeader(LOCATION, "/index.html");
                    response.setResponseHeader(SET_COOKIE, "JSESSIONID=" + session.getId());
                    return response.responseMessage();
                }
            }

            FileManager fileManager = FileManager.from("/401.html");
            String fileContent = fileManager.fileContent();
            String fileExtension = fileManager.extractFileExtension();

            response.setResponseStatus(UNAUTHORIZED);
            response.setResponseBody(fileContent);
            response.setResponseHeader(CONTENT_TYPE, HttpContentType.mimeTypeWithCharset(fileExtension, UTF_8));
            response.setResponseHeader(CONTENT_LENGTH, String.valueOf(response.measureContentLength()));
            return response.responseMessage();
        }

        if (request.consistsOf(GET, "/login", "/login.html") & request.hasQueryString()) {
            String account = request.getQueryStringValue("account");
            String password = request.getQueryStringValue("password");

            Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (user.checkPassword(password)) {
                    Session session = new Session(UUID.randomUUID().toString());
                    sessionManager.add(session);

                    response.setResponseStatus(FOUND);
                    response.setResponseHeader(LOCATION, "/index.html");
                    response.setResponseHeader(SET_COOKIE, "JSESSIONID=" + session.getId());
                    return response.responseMessage();
                }
            }

            FileManager fileManager = FileManager.from("/401.html");
            String fileContent = fileManager.fileContent();
            String fileExtension = fileManager.extractFileExtension();

            response.setResponseStatus(UNAUTHORIZED);
            response.setResponseBody(fileContent);
            response.setResponseHeader(CONTENT_TYPE, HttpContentType.mimeTypeWithCharset(fileExtension, UTF_8));
            response.setResponseHeader(CONTENT_LENGTH, String.valueOf(response.measureContentLength()));
            return response.responseMessage();
        }

        if (request.consistsOf(GET, "/login", "/login.html") & request.hasSessionId()) {
            String sessionId = request.sessionId();
            Session session = sessionManager.findSession(sessionId);

            if (session != null) {
                response.setResponseStatus(FOUND);
                response.setResponseHeader(LOCATION, "/index.html");
                return response.responseMessage();
            }
        }

        // register
        if (request.consistsOf(POST, "/register", "/register.html") & request.hasBody()) {
            String account = request.getBodyValue("account");
            String password = request.getBodyValue("password");
            String email = request.getBodyValue("email");

            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            Session session = new Session(UUID.randomUUID().toString());
            sessionManager.add(session);

            response.setResponseStatus(FOUND);
            response.setResponseHeader(LOCATION, "/index.html");
            response.setResponseHeader(SET_COOKIE, "JSESSIONID=" + session.getId());
            return response.responseMessage();
        }

        // 권한이 필요하지 않은 URI
        String uri = request.requestUri();

        FileManager fileManager = FileManager.from(uri);
        String fileContent = fileManager.fileContent();
        String fileExtension = fileManager.extractFileExtension();

        response.setResponseStatus(OK);
        response.setResponseBody(fileContent);
        response.setResponseHeader(CONTENT_TYPE, HttpContentType.mimeTypeWithCharset(fileExtension, UTF_8));
        response.setResponseHeader(CONTENT_LENGTH, String.valueOf(response.measureContentLength()));
        return response.responseMessage();
    }
}
