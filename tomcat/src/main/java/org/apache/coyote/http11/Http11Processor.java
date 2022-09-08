package org.apache.coyote.http11;

import static org.apache.coyote.http11.support.HttpStatus.NOT_FOUND;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.InvalidHttpRequestException;
import org.apache.coyote.http11.web.AuthenticationInterceptor;
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
import java.util.List;

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

    private HttpResponse handleRequest(final HttpRequest httpRequest) throws IOException {
        final List<String> includeUris = List.of("/login", "/register");
        if (!new AuthenticationInterceptor(includeUris).preHandle(httpRequest)) {
            return HttpResponse.sendRedirect("/index.html");
        }

        try {
            return new RequestHandler().handle(httpRequest);

        } catch (final NotFoundException e) {
            return HttpResponse.sendError(NOT_FOUND);
        }
    }

    private void sendResponse(final OutputStream outputStream, final HttpResponse httpResponse) throws IOException {
        final String formattedResponse = httpResponse.format();
        outputStream.write(formattedResponse.getBytes());
        outputStream.flush();
    }
}
