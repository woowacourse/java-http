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

    private static final Logger LOG = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Mapper mapper;
    private final HttpRequestParser httpRequestParser = new HttpRequestParser();
    private final HttpResponseGenerator httpResponseGenerator = new HttpResponseGenerator();

    public Http11Processor(final Socket connection, final Mapper mapper) {
        this.connection = connection;
        this.mapper = mapper;
    }

    @Override
    public void run() {
        LOG.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            execute(bufferedReader, outputStream);
        } catch (final IOException | UncheckedServletException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void execute(final BufferedReader bufferedReader, final OutputStream outputStream) throws IOException {
        try {
            final HttpRequest httpRequest = httpRequestParser.parse(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse(httpRequest.getRequestLine().getHttpVersion());

            mapper.service(httpRequest, httpResponse);

            final String generate = httpResponseGenerator.generate(httpResponse);
            outputStream.write(generate.getBytes());
            outputStream.flush();
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            internalServerError(outputStream);
        }
    }

    private void internalServerError(final OutputStream outputStream) throws IOException {
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1)
                .setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .sendRedirect("/500.html");
        final String response = httpResponseGenerator.generate(httpResponse);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
