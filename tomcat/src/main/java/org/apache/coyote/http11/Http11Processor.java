package org.apache.coyote.http11;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RequestMapping;
import com.techcourse.controller.StaticResourceController;
import com.techcourse.exception.UncheckedServletException;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private final Socket connection;
    private final RequestMapping requestMapping;
    private final HttpResponseWriter responseWriter;

    public Http11Processor(final Socket connection) {
        this(connection, new ResponseContentResolver(), SessionManager.getInstance());
    }

    Http11Processor(final Socket connection, final ResponseContentResolver responseContentResolver) {
        this(connection, responseContentResolver, SessionManager.getInstance());
    }

    Http11Processor(
            final Socket connection,
            final ResponseContentResolver responseContentResolver,
            final Manager sessionManager
    ) {
        this(connection, createRequestMapping(responseContentResolver, sessionManager));
    }

    Http11Processor(final Socket connection, final RequestMapping requestMapping) {
        this(connection, requestMapping, new HttpResponseWriter());
    }

    Http11Processor(
            final Socket connection,
            final RequestMapping requestMapping,
            final HttpResponseWriter responseWriter
    ) {
        this.connection = Objects.requireNonNull(connection);
        this.requestMapping = Objects.requireNonNull(requestMapping);
        this.responseWriter = Objects.requireNonNull(responseWriter);
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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final Optional<HttpRequest> parsedRequest;
            try {
                parsedRequest = HttpRequest.readFrom(reader);
            } catch (IOException e) {
                log.warn("Failed to read HTTP request", e);
                return;
            }
            if (parsedRequest.isEmpty()) {
                return;
            }

            final var request = parsedRequest.get();
            final var path = request.path();
            if (path == null) {
                return;
            }
            final var controller = requestMapping.getController(request);
            final var response = controller.service(request);

            try {
                responseWriter.write(response, outputStream);
            } catch (IOException e) {
                log.warn("Failed to write HTTP response", e);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error("Failed to handle HTTP connection", e);
        }
    }

    private static RequestMapping createRequestMapping(
            final ResponseContentResolver responseContentResolver,
            final Manager sessionManager
    ) {
        final var staticResourceController = new StaticResourceController(responseContentResolver);
        return new RequestMapping(
                Map.of(
                        LOGIN_PATH, new LoginController(staticResourceController, sessionManager),
                        REGISTER_PATH, new RegisterController(staticResourceController)),
                staticResourceController);
    }
}
