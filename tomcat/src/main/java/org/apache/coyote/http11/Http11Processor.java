package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.util.StreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final RequestProcessor requestProcessor = new RequestProcessor();

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
             final var outputStream = connection.getOutputStream()
        ) {
            final HttpRequest httpRequest = StreamReader.readRequest(inputStream);
            if (httpRequest == null) {
                return;
            }
            final HttpResponse response = requestProcessor.generateResponse(httpRequest);
            outputStream.write(response.convertByteArray());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
