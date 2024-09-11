package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.techcourse.controller.Controller;
import com.techcourse.controller.FrontController;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
            final var request = HttpRequest.from(reader);
            if (request.getRequestLine() == null) {
                return;
            }
            dispatch(request, outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void dispatch(final HttpRequest request, final OutputStream outputStream) throws Exception {
        log.info("request method: {}, path: {}", request.getRequestMethod(), request.getRequestPath());

        final HttpResponse response = new HttpResponse();
        Controller controller = new FrontController();
        controller.service(request, response);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
