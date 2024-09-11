package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping requestMapping = new RequestMapping();

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
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final var request = HttpRequest.from(bufferedReader);
            final var response = new HttpResponse();
            service(request, response);
            render(outputStream, request, response);
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void service(final HttpRequest request, final HttpResponse response) throws IOException {
        log.info("요청 = {}", request.getRequestLine());
        final var controller = requestMapping.getController(request);
        controller.service(request, response);
    }

    private void render(final OutputStream outputStream, final HttpRequest request, final HttpResponse response)
            throws IOException {
        outputStream.write(response.toHttpResponse(request).getBytes());
        outputStream.flush();
    }
}
