package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.HttpStatus;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final var httpRequest = HttpRequest.from(bufferedReader);
            log.debug(httpRequest.toStartLineString());
            final var httpResponse = new HttpResponse();

            doService(httpRequest, httpResponse);

            outputStream.write(httpResponse.toString().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doService(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            final var controller = RequestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            httpResponse.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
