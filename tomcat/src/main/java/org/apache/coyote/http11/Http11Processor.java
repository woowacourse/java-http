package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Adaptor adaptor;

    private final Socket connection;

    public Http11Processor(final Socket connection, final Adaptor adaptor) {
        this.connection = connection;
        this.adaptor = adaptor;
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
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest request = new HttpRequest(bufferedReader);

            Controller controller = adaptor.findController(request);
            HttpResponse response = controller.service(request, new HttpResponse());

            outputStream.write(response.format().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
