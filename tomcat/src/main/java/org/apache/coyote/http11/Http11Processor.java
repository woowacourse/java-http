package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Controller registerController = new RegisterController();
    private final Controller loginController = new LoginController();
    private final Controller staticResourceController = new StaticResourceController();

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
            HttpRequest request = HttpRequest.parse(inputStream);
            HttpResponse response = handleRequest(request);

            response.writeTo(outputStream);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(HttpRequest request) throws IOException {
        String method = request.requestLine().method();
        String path = request.requestLine().path();

        if ("GET".equals(method)) {
            return handleGetRequest(path, request);
        }

        if ("POST".equals(method)) {
            return handlePostRequest(path, request);
        }

        return HttpResponse.empty(405, "Method Not Allowed");
    }

    private HttpResponse handleGetRequest(String resourcePath, HttpRequest request) throws IOException {
        if ("/login".equals(resourcePath)) {
            return loginController.handle(request);
        }

        return staticResourceController.handle(request);
    }

    private HttpResponse handlePostRequest(String resourcePath, HttpRequest request) throws IOException {
        if (resourcePath.equals("/register")) {
            return registerController.handle(request);
        }

        if (resourcePath.equals("/login")) {
            return loginController.handle(request);
        }

        return HttpResponse.empty(405, "Method Not Allowed");
    }
}
