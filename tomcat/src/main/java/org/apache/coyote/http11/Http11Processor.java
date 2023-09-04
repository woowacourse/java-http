package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.Session;
import nextstep.jwp.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import static org.apache.coyote.http11.HttpHeaderType.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager sessionManager = new SessionManager();
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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequestParser httpRequestParser = new HttpRequestParser(reader);
            final HttpRequest httpRequest = httpRequestParser.parse();
            final HttpCookie httpCookie = HttpCookie.of(httpRequest.getHeaders().get("Cookie"));

            // TODO: generate RequestHandler
            final HttpResponse httpResponse = handleRequest(httpRequest);
            outputStream.write(httpResponse.stringify().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.getTarget().equals("/")) {
            return handleRootRequest();
        }

        if (httpRequest.getTarget().equals("/login")) {
            return handleLoginRequest(httpRequest);
        }

        if (httpRequest.getTarget().equals("/register")) {
            return handleRegisterRequest(httpRequest);
        }

        return handleResourceRequest(httpRequest, httpRequest.getTarget());
    }

    private HttpResponse handleRootRequest() {
        final HttpResponse httpResponse = HttpResponse.init();
        httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
        httpResponse.setStatusCode(OK);
        httpResponse.setBody("Hello world!");
        return httpResponse;
    }

    private HttpResponse handleRegisterRequest(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.getMethod().equals("GET")) {
            return handleResourceRequest(httpRequest, "register.html");
        }

        if (httpRequest.getMethod().equals("POST")) {
            final HttpResponse httpResponse = HttpResponse.init();
            final User newUser = new User(
                    httpRequest.getBody().get("account"),
                    httpRequest.getBody().get("password"),
                    httpRequest.getBody().get("email")
            );
            InMemoryUserRepository.save(newUser);
            httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
            httpResponse.addHeader(LOCATION, "/index.html");
            httpResponse.setStatusCode(FOUND);
            return httpResponse;
        }
        return handleResourceRequest(httpRequest, "404.html"); // Method Not Allowed
    }

    private HttpResponse handleLoginRequest(final HttpRequest httpRequest) throws IOException {
        final HttpCookie httpCookie = HttpCookie.of(httpRequest.getHeaders().get("Cookie"));
        String sessionId = httpCookie.getCookie("JSESSIONID");

        if (httpRequest.getMethod().equals("GET")) {
            final HttpResponse httpResponse = HttpResponse.init();

            final Session session = sessionManager.findSession(sessionId);
            if (session != null) {
                // already login user
                httpResponse.setStatusCode(FOUND);
                httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
                httpResponse.addHeader(LOCATION, "/index.html");
                return httpResponse;
            }
            // not login user
            return handleResourceRequest(httpRequest, "login.html");
        }

        if (httpRequest.getMethod().equals("POST")) {
            final HttpResponse httpResponse = HttpResponse.init();

            Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getBody().get("account"));
            if (user.isEmpty() || !user.get().checkPassword(httpRequest.getBody().get("password"))) {
                // invalid user
                httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
                httpResponse.addHeader(LOCATION, "/401.html");
                httpResponse.setStatusCode(FOUND);
                return httpResponse;
            }

            // valid user
            log.info("user: {}", user.get());

            if (sessionId != null) { // if already have session
                httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
                httpResponse.addHeader(LOCATION, "/index.html");
                httpResponse.setStatusCode(FOUND);
                return httpResponse;
            }

            // if no session
            Session session = new Session(String.valueOf(UUID.randomUUID()));
            session.setAttribute("user", user);
            sessionManager.add(session);
            sessionId = session.getId();

            httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
            httpResponse.addHeader(LOCATION, "/index.html");
            httpResponse.addHeader(SET_COOKIE, "JSESSIONID=" + sessionId);
            httpResponse.setStatusCode(FOUND);
            return httpResponse;
        }
        return handleResourceRequest(httpRequest, "404.html"); // Method Not Allowed
    }

    private HttpResponse handleResourceRequest(final HttpRequest httpRequest, final String resourceUrl) throws IOException {
        final HttpResponse httpResponse = HttpResponse.init();
        String contentType = "text/html;charset=utf-8";

        if (httpRequest.getHeaders().get("Accept") != null) {
            contentType = httpRequest.getHeaders().get("Accept").split(",")[0];
        }

        URL resource = getClass().getClassLoader().getResource("static/" + resourceUrl);
        if (resource != null) {
            httpResponse.setStatusCode(OK);
        } else {
            resource = getClass().getClassLoader().getResource("static/" + "404.html");
            httpResponse.setStatusCode(NOT_FOUND);
            contentType = "text/html;charset=utf-8";
        }

        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        httpResponse.addHeader(CONTENT_TYPE, contentType);
        httpResponse.setBody(responseBody);
        return httpResponse;
    }
}
