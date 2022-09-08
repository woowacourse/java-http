package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.controller.FrontController;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.request.HttpRequest;
import web.response.HttpResponse;

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
        try (final var bufferedReader =
                     new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = new HttpRequest(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();
            FrontController.getInstance().service(httpRequest, httpResponse);
            loggingHttpMessages(httpRequest, httpResponse);
            outputStream.write(httpResponse.createMessage().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void loggingHttpMessages(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        log.info("[ REQUEST  LINE : {} ]", httpRequest.getRequestLine().toString());
        log.info("[ REQUEST  BODY : {} ]", httpRequest.getBody().trim());
        log.info("[ RESPONSE LINE : {} ]", httpResponse.getStatusLine().toString());
    }
}
