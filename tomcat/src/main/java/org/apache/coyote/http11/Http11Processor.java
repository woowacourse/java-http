package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.handler.RequestHandler;
import org.apache.coyote.handler.ErrorHandler;
import org.apache.coyote.error.HttpException;
import org.apache.coyote.httpRequest.HttpRequest;
import org.apache.coyote.httpRequest.httpHeader.HttpHeader;
import org.apache.coyote.httpResponse.HttpResponse;
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
            final HttpResponse httpResponse = new HttpResponse();
            final HttpRequest httpRequest = new HttpRequest(inputStream);
            processRequest(httpRequest, httpResponse);
            writeResponse(outputStream, httpResponse);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processRequest(
            final HttpRequest httpRequest,
            final HttpResponse httpResponse
    ) throws IOException {
        try {
            final HttpHeader httpHeader = httpRequest.getHttpHeader();
            final Controller controller = RequestHandler.getController(httpHeader.getPurePath());
            controller.service(httpRequest, httpResponse);
        } catch (HttpException e) {
            ErrorHandler.handleError(e, httpResponse);
        } catch (Exception e) {
            ErrorHandler.handleServerError(httpResponse);
        }
    }

    private void writeResponse(
            final OutputStream outputStream,
            final HttpResponse httpResponse
    ) throws IOException {
        final String response = httpResponse.getResponse();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
