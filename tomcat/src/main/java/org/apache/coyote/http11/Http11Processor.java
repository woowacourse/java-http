package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.servlet.Servlet;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final List<Servlet> servlets;

    public Http11Processor(final Socket connection, final List<Servlet> servlets) {
        this.connection = connection;
        this.servlets = servlets;
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
            byte[] input = inputStream.readAllBytes();
            Servlet servlet = findServletFor(input);
            String response = servlet.handle(input);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Servlet findServletFor(byte[] input) {
        // TODO : 명확한 예외 타입 사용
        return servlets.stream()
                .filter(servlet -> servlet.canHandle(input))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
