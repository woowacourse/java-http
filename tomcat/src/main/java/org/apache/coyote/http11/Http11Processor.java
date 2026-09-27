package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final RequestMapping REQUEST_MAPPING = new RequestMapping();

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

            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.from(bufferedReader);
            HttpResponse response = new HttpResponse();

            Controller controller = REQUEST_MAPPING.getController(request);
            controller.service(request, response);

            response.writeTo(outputStream);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
