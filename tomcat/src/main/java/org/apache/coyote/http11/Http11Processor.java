package org.apache.coyote.http11;

import static org.apache.coyote.http11.support.HttpStatus.INTERNAL_SERVER_ERROR;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.InvalidHttpRequestException;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStatus;
import org.apache.coyote.http11.web.FileHandler;
import org.apache.coyote.http11.web.RequestHandler;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.request.HttpRequestParser;
import org.apache.coyote.http11.web.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedHashMap;

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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest httpRequest = HttpRequestParser.execute(bufferedReader);
            final HttpResponse httpResponse = handleRequest(httpRequest);

            sendResponse(outputStream, httpResponse);
        } catch (IOException | UncheckedServletException | InvalidHttpRequestException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(final HttpRequest httpRequest) {
        try {
            return chooseHandler(httpRequest);
        } catch (final IOException e) {
            final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
            final HttpStatus httpStatus = INTERNAL_SERVER_ERROR;
            return new HttpResponse(httpStatus, httpHeaders, httpStatus.getValue());
        }
    }

    private HttpResponse chooseHandler(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isStaticResource()) {
            return new FileHandler().handle(httpRequest);
        }
        return new RequestHandler().handle(httpRequest);
    }

    private void sendResponse(final OutputStream outputStream, final HttpResponse httpResponse) throws IOException {
        final String formattedResponse = httpResponse.format();
        outputStream.write(formattedResponse.getBytes());
        outputStream.flush();
    }
}
