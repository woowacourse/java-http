package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpResponse INTERNAL_SERVER_ERROR_RESPONSE = new HttpResponse(HttpVersion.HTTP_1_1)
            .setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
            .sendRedirect("/500.html");

    private final Socket connection;
    private final Adapter adapter;
    private final HttpRequestParser httpRequestParser = new HttpRequestParser();
    private final HttpResponseGenerator httpResponseGenerator = new HttpResponseGenerator();

    public Http11Processor(final Socket connection, final Adapter adapter) {
        this.connection = connection;
        this.adapter = adapter;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            execute(bufferedReader, outputStream);
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void execute(final BufferedReader bufferedReader, final OutputStream outputStream) throws IOException {
        try {
            final HttpRequest httpRequest = httpRequestParser.parse(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse(httpRequest.getHttpVersion());

            adapter.service(httpRequest, httpResponse);
            write(outputStream, httpResponse);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            write(outputStream, INTERNAL_SERVER_ERROR_RESPONSE);
        }
    }

    private void write(final OutputStream outputStream, final HttpResponse httpResponse) throws IOException {
        final String generate = httpResponseGenerator.generate(httpResponse);
        outputStream.write(generate.getBytes());
        outputStream.flush();
    }
}
