package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.http.ContentType;
import org.apache.http.Cookie;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ViewResolver;
import org.apache.session.Session;
import org.apache.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager = new SessionManager();

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
        try (final var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {
            final HttpRequest httpRequest = new HttpRequest(reader);
            final HttpResponse httpResponse = new HttpResponse(outputStream);

            controller(httpRequest, httpResponse);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void controller(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if ("GET".equals(httpRequest.getHttpMethod()) && "/".equals(httpRequest.getRequestURI())) {
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, "html");
            httpResponse.setHttpStatus(HttpStatus.OK);
            httpResponse.write("Hello world!");
            return;
        }
        if ("GET".equals(httpRequest.getHttpMethod()) && "/index.html".equals(httpRequest.getRequestURI())) {
            httpResponse.setHttpStatus(HttpStatus.OK);
            final ViewResolver viewResolver = new ViewResolver(Path.of(httpRequest.getRequestURI()));
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.of(viewResolver.getFileExtension()).getValue());
            httpResponse.write(Files.readString(viewResolver.getResourcePath()));
            return;
        }
        if ("POST".equals(httpRequest.getHttpMethod()) && "/login".equals(httpRequest.getRequestURI())) {
            final Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getRequestBody().get("account"));
            if (user.isEmpty()) {
                httpResponse.sendRedirect("/401.html");
                return;
            }
            if (user.get().checkPassword(httpRequest.getRequestBody().get("password"))) {
                log.info("user : {}", user);
                final String sessionId = UUID.randomUUID().toString();
                final Session session = new Session(sessionId);
                session.setAttribute("user", user);
                sessionManager.add(session);

                httpResponse.addCookie("JSESSIONID", sessionId);
                httpResponse.sendRedirect("/index.html");
                return;
            }
            httpResponse.sendRedirect("/401.html");
            return;
        }
        if ("GET".equals(httpRequest.getHttpMethod()) && "/login".equals(httpRequest.getRequestURI())) {
            Cookie cookie = Cookie.empty();
            if (httpRequest.getHttpHeaders().containsHeader("Cookie")) {
                cookie = Cookie.parse(httpRequest.getHttpHeaders().getHeaderValue("Cookie"));
            }
            if (cookie.containsKey("JSESSIONID")) {
                httpResponse.sendRedirect("/index.html");
                return;
            }

            httpResponse.setHttpStatus(HttpStatus.OK);
            final ViewResolver viewResolver = new ViewResolver(Path.of(httpRequest.getRequestURI()));
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.of(viewResolver.getFileExtension()).getValue());
            httpResponse.write(Files.readString(viewResolver.getResourcePath()));
            return;
        }
        if ("POST".equals(httpRequest.getHttpMethod()) && "/register".equals(httpRequest.getRequestURI())) {
            InMemoryUserRepository.save(new User(httpRequest.getRequestBody().get("account"), httpRequest.getRequestBody().get("password"), httpRequest.getRequestBody().get("email")));
            httpResponse.sendRedirect("/index.html");
            return;
        }
        if ("GET".equals(httpRequest.getHttpMethod()) && "/register".equals(httpRequest.getRequestURI())) {
            httpResponse.setHttpStatus(HttpStatus.OK);
            final ViewResolver viewResolver = new ViewResolver(Path.of(httpRequest.getRequestURI()));
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.of(viewResolver.getFileExtension()).getValue());
            httpResponse.write(Files.readString(viewResolver.getResourcePath()));
            return;
        }

        httpResponse.setHttpStatus(HttpStatus.OK);
        final ViewResolver viewResolver = new ViewResolver(Path.of(httpRequest.getRequestURI()));
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.of(viewResolver.getFileExtension()).getValue());
        httpResponse.write(Files.readString(viewResolver.getResourcePath()));
    }
}
