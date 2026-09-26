package org.apache.coyote.http11;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.web.StaticResourceHandler;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.*;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final LoginController loginController;
    private final RegisterController registerController;
    private final StaticResourceHandler staticResourceHandler;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.loginController = new LoginController();
        this.registerController = new RegisterController();
        this.staticResourceHandler = new StaticResourceHandler();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = new HttpRequest(bufferedReader);

            final HttpResponse response = handleRequest(httpRequest);

            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(final HttpRequest httpRequest) throws IOException {
        final RequestLine requestLine = httpRequest.getRequestLine();
        final HttpMethod httpMethod = requestLine.getHttpMethod();
        final String target = requestLine.getTarget();
        final URI uri = URI.create(target);
        final String uriPath = uri.getPath();
        final String requestBody = httpRequest.getBody().getContent();

        final RequestHeaders headers = httpRequest.getHeaders();
        final HttpCookie httpCookie = new HttpCookie(headers.get("Cookie"));

        if (uriPath.equals("/login")) {
            return loginController.handle(httpMethod, uri, httpCookie, requestBody);
        }

        if (uriPath.equals("/register")) {
            return registerController.handle(requestBody);
        }

        return staticResourceHandler.handle(uriPath);
    }

    private void writeResponse(final OutputStream outputStream, final HttpResponse response) throws IOException {
        log.info("response status: {}", response.status());
        outputStream.write(response.toHttpMessage().getBytes());
        outputStream.flush();
    }
}
