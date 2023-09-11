package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ControllerMapping;
import nextstep.jwp.controller.page.InternalServerErrorController;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequest request = HttpRequest.parse(bufferedReader);
            final HttpResponse response = HttpResponse.create();
            executeController(request, response);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void executeController(final HttpRequest request, final HttpResponse response) throws IOException {
        try {
            final Controller controller = ControllerMapping.find(request);
            controller.service(request, response);
        } catch (final IllegalArgumentException e) {
            InternalServerErrorController.create(request, response);
        }
    }
}
