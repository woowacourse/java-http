package org.apache.coyote.http11;

import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpRequestParser;
import org.apache.coyote.http11.model.HttpResponse;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequest request = HttpRequestParser.from(inputStream)
                .toHttpRequest();

            Controller controller = RequestMapping.getController(request.getRequestURI());
            HttpResponse httpResponse = controller.service(request);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
