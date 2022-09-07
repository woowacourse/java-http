package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import org.apache.controller.Controller;
import org.apache.controller.RequestMapping;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.coyote.Processor;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

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
            HttpRequest httpRequest = HttpRequest.parse(bufferedReader);

            final Controller controller = RequestMapping.findController(httpRequest.getUrl());
            final HttpResponse httpResponse = controller.service(httpRequest);

            outputStream.write(httpResponse.convertTemplate().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
