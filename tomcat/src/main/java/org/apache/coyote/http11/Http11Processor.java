package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.controller.LoginController;
import org.apache.coyote.controller.RegisterController;
import org.apache.coyote.controller.StaticFileController;
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
            AbstractController loginHandler = new LoginController();
            AbstractController registerHandler = new RegisterController();
            AbstractController staticFileConroller = new StaticFileController();

            Http11Response response = new Http11Response();
            if (path.startsWith("/login")) {
                loginHandler.service(request, response);
            } else if (path.startsWith("/register")) {
                registerHandler.service(request, response);
            } else {
                staticFileConroller.service(request, response);
            }

            outputStream.write(response.getResponseHeader().getBytes(StandardCharsets.UTF_8));
            if (response.getResponseBody() != null) {
                outputStream.write(response.getResponseBody());
            }
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
