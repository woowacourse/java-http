package org.apache.coyote.http11;

import com.techcourse.exception.BadRequestException;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;
import org.apache.catalina.servlet.HttpServletContainer;
import org.apache.coyote.Processor;
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final HttpResponse response = processResponse(reader);

            final byte[] output = HttpResponseParser.parse(response);
            outputStream.write(output);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse processResponse(BufferedReader reader) throws IOException {
        final HttpResponse response = new HttpResponse();
        try {
            final HttpRequest request = HttpRequestParser.parse(reader);
            HttpServletContainer.handle(request, response);

            return response;
        } catch (BadRequestException e) {
            ResponseProcessor.handleBadRequest(response);
            return response;
        }
    }
}
