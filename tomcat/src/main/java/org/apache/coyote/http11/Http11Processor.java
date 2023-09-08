package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.coyote.http11.request.line.HttpMethod.GET;
import static org.apache.coyote.http11.request.line.HttpMethod.POST;
import static org.apache.coyote.http11.response.header.HttpHeader.SET_COOKIE;
import static org.apache.coyote.http11.response.line.ResponseStatus.FOUND;
import static org.apache.coyote.http11.response.line.ResponseStatus.OK;
import static org.apache.coyote.http11.response.line.ResponseStatus.UNAUTHORIZED;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.service.AuthService;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
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
                final var reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8));
                final var outputStream = connection.getOutputStream()
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
        AuthService authService = new AuthService(sessionManager);

        if (request.consistsOf(GET, "/")) {
            response.setResponseMessage(OK, DEFAULT_BODY);
            return response.responseMessage();
        }

        if (request.consistsOf(POST, "/login", "/login.html")) {
            String account = request.getBodyValue("account");
            String password = request.getBodyValue("password");

            return processLogin(response, authService, account, password);
        }

        if (request.consistsOf(GET, "/login", "/login.html") & request.hasQueryString()) {
            String account = request.getQueryStringValue("account");
            String password = request.getQueryStringValue("password");

            return processLogin(response, authService, account, password);
        }

        if (request.consistsOf(GET, "/login", "/login.html") & request.hasSessionId()) {
            String sessionId = request.sessionId();
            Session session = sessionManager.findSession(sessionId);

            if (session != null) {
                response.setResponseRedirect(FOUND, "/index.html");
                return response.responseMessage();
            }
        }

        if (request.consistsOf(POST, "/register", "/register.html") & request.hasBody()) {
            return processRegister(request, response, authService);
        }

        response.setResponseResource(OK, request.requestUri());
        return response.responseMessage();
    }

    private String processLogin(HttpResponse response, AuthService authService, String account, String password) {
        try {
            String sessionId = authService.login(account, password);
            response.setResponseRedirect(FOUND, "/index.html");
            response.setResponseHeader(SET_COOKIE, "JSESSIONID=" + sessionId);
            return response.responseMessage();
        } catch (IllegalArgumentException e) {
            response.setResponseResource(UNAUTHORIZED, "/401.html");
            return response.responseMessage();
        }
    }

    private String processRegister(HttpRequest request, HttpResponse response, AuthService authService) {
        String account = request.getBodyValue("account");
        String password = request.getBodyValue("password");
        String email = request.getBodyValue("email");

        String sessionId = authService.register(account, password, email);
        response.setResponseRedirect(FOUND, "/index.html");
        response.setResponseHeader(SET_COOKIE, "JSESSIONID=" + sessionId);
        return response.responseMessage();
    }
}
