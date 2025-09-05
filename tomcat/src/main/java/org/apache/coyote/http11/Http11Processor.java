package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handle.handler.HttpHandler;
import org.apache.coyote.http11.handle.HttpHandlerMapper;
import org.apache.coyote.http11.reqeust.HttpRequest;
import org.apache.coyote.http11.reqeust.util.HttpRequestReader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.util.HttpResponseWriter;
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()
        ) {
            final HttpRequestReader reader = new HttpRequestReader(inputStream);
            final HttpResponseWriter writer = new HttpResponseWriter(outputStream);

            final HttpRequest request = reader.read();
            final HttpHandlerMapper handlerMapper = HttpHandlerMapper.getInstance();
            final HttpHandler handler = handlerMapper.getHandler(request);
            final HttpResponse response = handler.handle(request);
            writer.write(response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
