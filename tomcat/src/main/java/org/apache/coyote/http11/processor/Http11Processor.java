package org.apache.coyote.http11.processor;

import java.io.IOException;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestConvertor;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest request = HttpRequestConvertor.convert(inputStream);
            final HttpResponse response = createResponse(request);
            outputStream.write(response.writeValueAsBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponse(final HttpRequest request) {
        final Controller controller = requestMapping.getController(request);
        final HttpResponse response = controller.doService(request);

        addContentTypeToResponse(request, response);
        addCookieForSessionToResponse(request, response);

        return response;
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
}
