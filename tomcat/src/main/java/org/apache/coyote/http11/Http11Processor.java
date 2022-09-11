package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.support.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.controller.RequestMapping;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOG = LoggerFactory.getLogger(Http11Processor.class);

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
        try (final var inputStreamReader = new InputStreamReader(connection.getInputStream());
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest request = RequestReader.readHttpRequest(bufferedReader);
            final Controller controller = RequestMapping.from(request.getPath());
            final HttpResponse response = controller.service(request);

            outputStream.write(response.asFormat().getBytes());
            outputStream.flush();
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
