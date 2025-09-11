package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.RootController;
import org.apache.catalina.controller.StaticController;
import org.apache.coyote.Processor;
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
        try (
                final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream()
        ) {
            final RootController rootController = new RootController();
            final LoginController loginController = new LoginController();
            final RegisterController registerController = new RegisterController();
            final StaticController staticController = new StaticController();

            final Request request = new Request(inputStream);
            final Response response = new Response(outputStream);

            final String requestURI = request.getRequestURI();
            if (requestURI.equals("/")) {
                rootController.service(request, response);
                return;
            }
            if (requestURI.equals("/login")) {
                loginController.service(request, response);
                return;
            }
            if (requestURI.equals("/register")) {
                registerController.service(request, response);
                return;
            }
            staticController.service(request, response);
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (final Exception e) {
            log.error(e.getMessage());
        }
    }
}
