package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import nextstep.jwp.controller.StaticResourceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestConvertor httpRequestConvertor = new HttpRequestConvertor();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        final HttpRequest request = createRequest(connection);
        final HttpResponse response = createResponse(request);
        writeResponseToSocket(connection, response);
    }

    private HttpRequest createRequest(final Socket connection) {
        try (final var inputStream = connection.getInputStream()) {
            return httpRequestConvertor.convert(inputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new UncheckedServletException(e);
        }
    }

    private HttpResponse createResponse(final HttpRequest request) {
        final HttpResponse response = findController(request).doService(request);

        addContentTypeToResponse(request, response);
        addCookieForSessionToResponse(request, response);

        return response;
    }

    private Controller findController(final HttpRequest request) {
        if (request.getUriPath().equals("/")) {
            return new HomeController();
        }

        if (request.getUriPath().equals("/login")) {
            return new LoginController();
        }

        if (request.getUriPath().equals("/register")) {
            return new RegisterController();
        }

        return new StaticResourceController();
    }

    private void addContentTypeToResponse(final HttpRequest request, final HttpResponse response) {
        if (response.hasBody()) {
            response.addHeader("Content-Type", new ContentTypeExtractor().extract(request).getValue());
        }
    }

    private void addCookieForSessionToResponse(final HttpRequest request, final HttpResponse response) {
        if (request.hasSession()) {
            response.addCookie("JSESSIONID", request.getSession().getId());
        }
    }

    private void writeResponseToSocket(final Socket connection, final HttpResponse response) {
        try (final var outputStream = connection.getOutputStream()) {
            outputStream.write(response.writeValueAsBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
            throw new UncheckedServletException(e);
        }
    }
}
