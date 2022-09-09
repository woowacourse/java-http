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
        final Thread thread = Thread.currentThread();
        log.info("ThreadName: {}", thread.getName());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final Request request = RequestParser.parse(bufferedReader);
            final Response response = new Response();

            if (loginInterceptor.preHandle(request, response, outputStream)) {
                process(request, response, outputStream);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private void process(final Request request, final Response response, final OutputStream outputStream) {
        try {
            final Controller controller = controllerMapping.getController(request.getRequestInfo());
            controller.service(request, response);
        } catch (UnauthorizedException e) {
            makeRedirectResponse(View.UNAUTHORIZED.getValue(), response);
        } catch (CustomNotFoundException e) {
            makeErrorResponse(HttpStatus.BAD_REQUEST, View.NOT_FOUND, response);
        } catch (Exception e) {
            makeErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, View.INTERNAL_SERVER_ERROR, response);
        }
        ResponseFlusher.flush(outputStream, response);
    }

    private void makeRedirectResponse(final String redirectUri, final Response response) {
        response.header(HttpHeader.LOCATION, redirectUri)
                .httpStatus(HttpStatus.FOUND);
    }

    private void makeErrorResponse(final HttpStatus httpStatus, final View errorView, final Response response) {
        final Resource resource = new Resource(errorView.getValue());
        response.header(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue())
                .httpStatus(httpStatus)
                .content(resource.read());
    }
}
