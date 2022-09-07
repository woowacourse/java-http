package org.apache.coyote.http11;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.CustomNotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.*;
import nextstep.jwp.support.Resource;
import nextstep.jwp.support.View;
import org.apache.coyote.Processor;
import org.apache.http.HttpHeader;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ControllerMapping controllerMapping;
    private final LoginInterceptor loginInterceptor = new LoginInterceptor(List.of("/login", "/register"));

    public Http11Processor(final Socket connection, final ControllerMapping controllerMapping) {
        this.connection = connection;
        this.controllerMapping = controllerMapping;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final Request request = RequestParser.parse(bufferedReader);
            if (loginInterceptor.preHandle(request, outputStream)) {
                process(request, outputStream);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private void process(final Request request, final OutputStream outputStream) {
        try {
            final Controller controller = controllerMapping.getController(request.getRequestInfo());
            ResponseFlusher.flush(outputStream, controller.execute(request));
        } catch (UnauthorizedException e) {
            ResponseFlusher.flush(outputStream, makeRedirectResponse(HttpStatus.FOUND, View.UNAUTHORIZED.getValue()));
        } catch (CustomNotFoundException e) {
            ResponseFlusher.flush(outputStream, makeErrorResponse(HttpStatus.BAD_REQUEST, View.NOT_FOUND));
        } catch (Exception e) {
            ResponseFlusher.flush(outputStream, makeErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, View.INTERNAL_SERVER_ERROR));
        }
    }

    private Response makeRedirectResponse(final HttpStatus httpStatus, final String redirectUri) {
        final Headers headers = new Headers();
        headers.put(HttpHeader.LOCATION, redirectUri);
        return new Response(headers).httpStatus(httpStatus);
    }

    private Response makeErrorResponse(final HttpStatus httpStatus, final View errorView) {
        final Resource resource = new Resource(errorView.getValue());
        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue());
        return new Response(headers).httpStatus(httpStatus)
                .content(resource.read());
    }
}
