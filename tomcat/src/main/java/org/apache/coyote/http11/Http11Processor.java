package org.apache.coyote.http11;

import nextstep.jwp.Controller;
import nextstep.jwp.exception.ResourceNotFoundException;
import nextstep.jwp.exception.UnsupportedMethodException;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestMapping;
import org.apache.coyote.http11.header.RequestLine;
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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final HttpRequest request = HttpRequest.parse(bufferedReader);
            final RequestLine requestLine = request.getRequestLine();
            log.info("{} {}", requestLine.getMethod(), requestLine.getUri());
            final Controller controller = RequestMapping.getController(request);
            final HttpResponse response = new HttpResponse();
            controller.service(request, response);
            log.info("{} {} {}", response.getStatus(), requestLine.getMethod(), request.getPath());
            writeResponse(outputStream, response.build());
        } catch (UnsupportedMethodException | IllegalArgumentException | ResourceNotFoundException e) {
            log.info("{}", e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
