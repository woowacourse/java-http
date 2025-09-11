package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestMapping;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpRequestBuilder;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final RequestMapping requestMapping = new RequestMapping();
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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final HttpRequest request = HttpRequestBuilder.read(bufferedReader);

            log.info("{} {} | message: {} {}", request.getMethod(), request.getPath(), request.getHeaders(), request.getBody());

            final Controller controller = requestMapping.getController(request);
            final HttpResponse response = new HttpResponse();
            controller.service(request, response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

