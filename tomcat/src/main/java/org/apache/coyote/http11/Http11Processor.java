package org.apache.coyote.http11;

import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

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
        try (final var inputStreamReader = new InputStreamReader(connection.getInputStream());
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final OutputStream outputStream = connection.getOutputStream()) {
            final var httpRequest = HttpRequest.from(bufferedReader);

            final String response = handleRequest(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String handleRequest(final HttpRequest httpRequest) throws IOException {
        final String uri = httpRequest.getRequestLine().getPath();
        final String path = uri.split("\\?")[0];

        final var httpResponse = HttpResponse.createEmpty();
        if (path.equals("/")) {
            final var homeController = new HomeController();
            homeController.service(httpRequest, httpResponse);
            return httpResponse.toString();
        }
        if (path.equals("/login")) {
            final var loginController = new LoginController();
            loginController.service(httpRequest, httpResponse);
            return httpResponse.toString();
        }
        if (path.equals("/register")) {
            final var registerController = new RegisterController();
            registerController.service(httpRequest, httpResponse);
            return httpResponse.toString();
        }
        final var resourceController = new ResourceController();
        resourceController.service(httpRequest, httpResponse);
        return httpResponse.toString();
    }
}
