package org.apache.coyote.http11;

import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Mapper mapper;

    public Http11Processor(final Socket connection, final Mapper mapper) {
        this.connection = connection;
        this.mapper = mapper;
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

            HttpRequest request = HttpRequest.of(inputStream);
            HttpResponse response = new HttpResponse();

            Controller controller = mapper.getController(request);
            controller.service(request, response);

            outputStream.write(response.generateResponse());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
