package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
        try (OutputStream outputStream = connection.getOutputStream();
             InputStream inputStream = connection.getInputStream()) {
            Http11RequestHeader http11RequestHeader = Http11RequestHeader.from(inputStream);
            Http11Response http11Response = getHttp11Response(http11RequestHeader);
            final var response = http11Response.getResponse();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static Http11Response getHttp11Response(Http11RequestHeader http11RequestHeader) throws IOException {
        String requestUri = http11RequestHeader.getRequestUri();
        String firstValueAccept = http11RequestHeader.getFirstValueAccept();
        StatusLine statusLine = StatusLine.ok(http11RequestHeader.getHttpVersion());
        return Http11Response.of(statusLine, firstValueAccept, requestUri);
    }
}
