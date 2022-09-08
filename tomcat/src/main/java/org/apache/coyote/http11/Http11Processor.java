package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.RequestMapping;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private final Logger log;
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.log = LoggerFactory.getLogger(getClass());
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

            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final HttpResponse response = route(httpRequest);
            final String responseAsString = response.toString();

            outputStream.write(responseAsString.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse route(final HttpRequest httpRequest) {
        final RequestMapping requestMapping = new RequestMapping();
        final Controller controller = requestMapping.getController(httpRequest);

        final HttpResponse httpResponse = new HttpResponse();
        controller.service(httpRequest, httpResponse);
        return httpResponse;
    }
}
