package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpStatusCode.BAD_REQUEST;
import static org.apache.coyote.http11.HttpStatusCode.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.HttpStatusCode.METHOD_NOT_ALLOWED;
import static org.apache.coyote.http11.HttpStatusCode.NOT_FOUND;
import static org.apache.coyote.http11.HttpStatusCode.UNAUTHORIZED;
import static org.apache.coyote.http11.util.FileLoader.loadFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.badrequest.BadRequestException;
import org.apache.coyote.http11.exception.methodnotallowed.MethodNotAllowedException;
import org.apache.coyote.http11.exception.notfound.NotFoundException;
import org.apache.coyote.http11.exception.unauthorized.UnAuthorizedException;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpReader httpReader = new HttpReader(bufferedReader);
            final HttpRequest httpRequest = new HttpRequest(httpReader);
            final HttpResponse httpResponse = new HttpResponse();
            handleRequest(httpRequest, httpResponse);
            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequest(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        try {
            final RequestMapping requestMapping = new RequestMapping();
            final Controller controller = requestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);
        } catch (Exception exception) {
            handleException(httpRequest, httpResponse, exception);
        }
    }

    private void handleException(final HttpRequest httpRequest, final HttpResponse httpResponse,
                                 final Exception exception) throws IOException {
        if (exception instanceof BadRequestException) {
            doException(httpRequest, httpResponse, BAD_REQUEST);
            return;
        }
        if (exception instanceof UnAuthorizedException) {
            doException(httpRequest, httpResponse, UNAUTHORIZED);
            return;
        }
        if (exception instanceof NotFoundException) {
            doException(httpRequest, httpResponse, NOT_FOUND);
            return;
        }
        if (exception instanceof MethodNotAllowedException) {
            doException(httpRequest, httpResponse, METHOD_NOT_ALLOWED);
            return;
        }
        doException(httpRequest, httpResponse, INTERNAL_SERVER_ERROR);
    }

    private void doException(final HttpRequest httpRequest, final HttpResponse httpResponse,
                             final HttpStatusCode httpStatusCode) throws IOException {
        final String responseBody = loadFile("/" + httpStatusCode.getCode() + ".html");
        httpResponse.statusCode(httpStatusCode)
                .addHeader(CONTENT_TYPE, ContentType.of(httpRequest.getFileExtension()).getValue())
                .addHeader(CONTENT_LENGTH, responseBody.getBytes().length)
                .responseBody(responseBody);
    }
}
