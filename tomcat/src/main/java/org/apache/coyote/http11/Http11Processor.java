package org.apache.coyote.http11;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest request = HttpRequest.from(new BufferedReader(new InputStreamReader(inputStream)));
            final Controller controller = RequestMapping.getController(request);

            final HttpResponse response = HttpResponse.create();
            controller.service(request, response);

            final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(response.toMessage());
            bufferedWriter.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
