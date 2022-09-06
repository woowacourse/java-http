package org.apache.coyote.http11;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.CustomNotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.RequestParser;
import nextstep.jwp.http.Response;
import nextstep.jwp.support.Resource;
import nextstep.jwp.support.View;
import org.apache.coyote.Processor;
import org.apache.http.HttpHeader;
import org.apache.http.HttpStatus;
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
    private final ControllerMapping controllerMapping = new ControllerMapping();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
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
            process(outputStream, bufferedReader);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private void process(final OutputStream outputStream, final BufferedReader bufferedReader) {
        try {
            final Request request = RequestParser.parse(bufferedReader);
            final Controller controller = controllerMapping.getController(request.getRequestInfo());
            flushResponse(outputStream, makeResponse(controller, request));
        } catch (UnauthorizedException e) {
            flushResponse(outputStream, makeRedirectResponse(HttpStatus.FOUND, View.UNAUTHORIZED.getValue()));
        } catch (CustomNotFoundException e) {
            flushResponse(outputStream, makeErrorResponse(HttpStatus.BAD_REQUEST, View.NOT_FOUND));
        } catch (Exception e) {
            flushResponse(outputStream, makeErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, View.INTERNAL_SERVER_ERROR));
        }
    }

    private String makeResponse(final Controller controller, final Request request) {
        return controller.execute(request)
                .parse();
    }

    private String makeRedirectResponse(final HttpStatus httpStatus, final String redirectUri) {
        final Headers headers = new Headers();
        headers.put(HttpHeader.LOCATION, redirectUri);
        return new Response(headers).httpStatus(httpStatus)
                .parse();
    }

    private String makeErrorResponse(final HttpStatus httpStatus, final View errorView) {
        final Resource resource = new Resource(errorView.getValue());
        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_TYPE, resource.getContentType().getValue());
        return new Response(headers).httpStatus(httpStatus)
                .content(resource.read())
                .parse();
    }

    private void flushResponse(final OutputStream outputStream, final String responseBody) {
        if (outputStream == null) {
            return;
        }
        try {
            outputStream.write(responseBody.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
