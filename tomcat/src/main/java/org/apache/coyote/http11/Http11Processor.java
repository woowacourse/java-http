package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.Session;
import nextstep.jwp.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = HttpRequest.of(new BufferedReader(new InputStreamReader(inputStream)));
            final HttpCookie httpCookie = HttpCookie.of(httpRequest.getHeader().get("Cookie"));
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
        final Map<String, String> responseHeader = new HashMap<>();
        responseHeader.put("Content-Type", "text/html;charset=utf-8");
        return HttpResponse.of("200 OK", responseHeader, "Hello world!");
    }

    private HttpResponse handleRegisterRequest(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.getMethod().equals("GET")) {
            return handleResourceRequest(httpRequest, "register.html");
        }

        if (httpRequest.getMethod().equals("POST")) {
            final User newUser = new User(
                    httpRequest.getBody().get("account"),
                    httpRequest.getBody().get("password"),
                    httpRequest.getBody().get("email")
            );

            InMemoryUserRepository.save(newUser);
            final Map<String, String> responseHeader = new HashMap<>();
            responseHeader.put("Content-Type", "text/html;charset=utf-8");
            responseHeader.put("Location", "/index.html");
            return HttpResponse.of("302", responseHeader, "");
        }
        return handleResourceRequest(httpRequest, "404.html"); // Method Not Allowed
    }

    private HttpResponse handleLoginRequest(final HttpRequest httpRequest) throws IOException {
        final HttpCookie httpCookie = HttpCookie.of(httpRequest.getHeader().get("Cookie"));
        String sessionId = httpCookie.getCookie("JSESSIONID");

        if (httpRequest.getMethod().equals("GET")) {
            final Session session = sessionManager.findSession(sessionId);
            if (session != null) {
                // already login user
                final Map<String, String> responseHeader = new HashMap<>();
                responseHeader.put("Content-Type", "text/html;charset=utf-8");
                responseHeader.put("Location", "/index.html");
                return HttpResponse.of("302", responseHeader, "");
            }
            // not login user
            return handleResourceRequest(httpRequest, "login.html");
        }

        if (httpRequest.getMethod().equals("POST")) {
            final Map<String, String> responseHeader = new HashMap<>();
            Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getBody().get("account"));
            if (user.isEmpty() || !user.get().checkPassword(httpRequest.getBody().get("password"))) {
                // invalid user
                responseHeader.put("Content-Type", "text/html;charset=utf-8");
                responseHeader.put("Location", "/401.html");
                return HttpResponse.of("302", responseHeader, "");
            }

            // valid user
            log.info("user: {}", user.get());

            if (sessionId != null) { // if already have session
                responseHeader.put("Content-Type", "text/html;charset=utf-8");
                responseHeader.put("Location", "/index.html");
                return HttpResponse.of("302", responseHeader, "");
            }

            // if no session
            Session session = new Session(String.valueOf(UUID.randomUUID()));
            session.setAttribute("user", user);
            sessionManager.add(session);
            sessionId = session.getId();

            responseHeader.put("Content-Type", "text/html;charset=utf-8");
            responseHeader.put("Location", "/index.html");
            responseHeader.put("Set-Cookie", "JSESSIONID=" + sessionId);
            return HttpResponse.of("302", responseHeader, "");
        }
        return handleResourceRequest(httpRequest, "404.html"); // Method Not Allowed
    }

    private HttpResponse handleResourceRequest(final HttpRequest httpRequest, final String resourceUrl) throws IOException {
        final Map<String, String> responseHeader = new HashMap<>();
        String statusCode;
        String contentType = "text/html;charset=utf-8";

        if (httpRequest.getHeader().get("Accept") != null) {
            contentType = httpRequest.getHeader().get("Accept").split(",")[0];
        }

        URL resource = getClass().getClassLoader().getResource("static/" + resourceUrl);
        if (resource != null) {
            statusCode = "200 OK";
        } else {
            resource = getClass().getClassLoader().getResource("static/" + "404.html");
            statusCode = "404";
            contentType = "text/html;charset=utf-8";
        }

        responseHeader.put("Content-Type", contentType);
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return HttpResponse.of(statusCode, responseHeader, responseBody);
    }
}
