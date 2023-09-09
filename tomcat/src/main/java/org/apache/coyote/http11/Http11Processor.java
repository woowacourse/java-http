package org.apache.coyote.http11;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.Processor;
import org.apache.coyote.publisher.InputStreamRequestPublisher;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();) {

            final HttpRequest request = InputStreamRequestPublisher.read(inputStream).toHttpRequest();
            log.info("=============> HTTP Request : \n {}", request);

            final HttpResponse response = HttpResponse.empty();
            final Controller controller = RequestMapping.getController(request);
            controller.service(request, response);
            log.info("=============> HTTP Response : \n {}", response);

            outputStream.write( Http11ResponseConverter.convertToBytes(response));
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
