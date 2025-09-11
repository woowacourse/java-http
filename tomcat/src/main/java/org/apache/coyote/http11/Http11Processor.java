package org.apache.coyote.http11;

import com.techcourse.WebApplicationInitializer;
import org.apache.catalina.controller.RequestAdapter;
import org.apache.coyote.Processor;
import org.apache.coyote.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestAdapter requestAdapter;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestAdapter = new RequestAdapter(WebApplicationInitializer.getRequestMapping());
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            final var request = HttpRequest.from(reader);
            final var response = requestAdapter.service(request);

            response.writeTo(connection.getOutputStream());
        } catch (Exception e) {
            log.error("Fatal error while processing connection", e);
        }
    }
}
