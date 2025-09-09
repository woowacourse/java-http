package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.LoginHandler;
import org.apache.coyote.controller.RegisterHandler;
import org.apache.coyote.controller.StaticFileHandler;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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

            final Http11Request request = new Http11Request(inputStream);
            String path = request.getPath();
            SessionManager sessionManager = new SessionManager();//TODO: SessionManager 생성 시점 고민

            Http11Response response;
            if (path.startsWith("/login")) {
                response = LoginHandler.getResponse(request, sessionManager);
            } else if (path.startsWith("/register")) {
                response = RegisterHandler.getResponse(request, sessionManager);
            } else {
                response = StaticFileHandler.getResponse(request, sessionManager);
            }

            outputStream.write(response.getResponseHeader().getBytes(StandardCharsets.UTF_8));
            if (response.getResponseBody() != null) {
                outputStream.write(response.getResponseBody());
            }
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
