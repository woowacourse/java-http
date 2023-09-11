package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

import static org.apache.coyote.http11.common.HttpStatus.INTERNAL_SERVER_ERROR;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpResponseParser httpResponseParser = new HttpResponseParser();
    private final Socket connection;
    private final Servlet servlet;

    public Http11Processor(final Socket connection, final Servlet servlet) {
        this.connection = connection;
        this.servlet = servlet;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
            generate(reader, writer);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void generate(final BufferedReader reader, final BufferedWriter writer) throws IOException {
        try {
            HttpRequest httpRequest = HttpRequestParser.generate(reader);
            HttpResponse httpResponse = new HttpResponse();
            servlet.service(httpRequest, httpResponse);
            writer.write(httpResponseParser.generate(httpResponse));
            writer.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            serverError(writer);
        }
    }

    private void serverError(final BufferedWriter writer) throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.httpStatus(INTERNAL_SERVER_ERROR)
                .redirectUri("/500.html");
        writer.write(httpResponseParser.generate(httpResponse));
        writer.flush();
    }

}
