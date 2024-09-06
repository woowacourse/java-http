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
    private final RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        requestHandlerMapper.register(new LoginRequestHandler());
        requestHandlerMapper.register(new StaticResourceRequestHandler());
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(inputStream);
            log.info("Request: {}", request);
            HttpResponse response = requestHandlerMapper.handle(request);
            outputStream.write(HttpResponseWriter.write(response).getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
