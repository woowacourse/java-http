package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
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
            final HttpRequest httpRequest = httpRequestParser.parse(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse(httpRequest.getRequestLine().getHttpVersion());
            mapper.service(httpRequest, httpResponse);
            final String generate = httpResponseGenerator.generate(httpResponse);
            outputStream.write(generate.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
