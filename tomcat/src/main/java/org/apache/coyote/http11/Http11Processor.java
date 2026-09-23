package org.apache.coyote.http11;

import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final byte[] HELLO_WORLD = "Hello world!".getBytes(StandardCharsets.UTF_8);

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapping = new RequestMapping(SessionManager.getInstance());
    }


    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final BufferedInputStream inputStream =
                     new BufferedInputStream(connection.getInputStream());
             final OutputStream outputStream = connection.getOutputStream()) {

            final Optional<HttpRequest> optionalRequest = HttpRequest.from(inputStream);

            if (optionalRequest.isEmpty()) {
                return;
            }
            final HttpRequest request = optionalRequest.get();
            final HttpResponse response = new HttpResponse();

            addSessionIdCookieIfAbsent(request, response);
            serviceController(request, response);
            if (!response.hasStatus()) {
                handleResource(request, response);
            }
            response.writeTo(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void addSessionIdCookieIfAbsent(
            final HttpRequest request, final HttpResponse response) {
        final Optional<String> existingSessionId = request.getCookie(JSESSIONID);

        if (existingSessionId.isPresent() && !existingSessionId.get().isBlank()) {
            return;
        }

        final String newSessionId = UUID.randomUUID().toString();

        response.addHeader(SET_COOKIE, JSESSIONID + "=" + newSessionId);
    }

    private void serviceController(
            final HttpRequest request, final HttpResponse response) throws Exception {
        final Optional<Controller> controller = requestMapping.getController(request);

        if (controller.isEmpty()) {
            return;
        }
        controller.get().service(request, response);
    }


    private void handleResource(
            final HttpRequest request,
            final HttpResponse response
    ) throws IOException, URISyntaxException {
        if ("/".equals(request.getPath())) {
            response.ok("text/html;charset=utf-8", HELLO_WORLD);
            return;
        }

        setStaticResourceResponse(response, request.getPath());
    }

    private void setStaticResourceResponse(
            final HttpResponse response,
            final String path
    ) throws IOException, URISyntaxException {
        final String resourcePath = resolveResourcePath(path);
        final URL resource = getClass().getClassLoader().getResource(resourcePath);

        if (resource == null) {
            setNotFoundResponse(response);
            return;
        }

        final byte[] responseBody = Files.readAllBytes(Path.of(resource.toURI()));
        response.ok(resolveContentType(path), responseBody);
    }

    private void setNotFoundResponse(final HttpResponse response) {
        final byte[] responseBody = "Not Found".getBytes(StandardCharsets.UTF_8);
        response.notFound("text/plain;charset=utf-8", responseBody);
    }

    private String resolveResourcePath(
            final String path
    ) {

        if ("/login".equals(path)) {
            return "static/login.html";
        }

        if ("/register".equals(path)) {
            return "static/register.html";
        }

        return "static" + path;
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }

        if (path.endsWith(".js")) {
            return "application/javascript";
        }

        return "text/html;charset=utf-8";
    }
}

