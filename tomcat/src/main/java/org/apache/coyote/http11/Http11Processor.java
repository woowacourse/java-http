package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.dispatcher.DispatcherHandler;
import org.apache.coyote.http11.dispatcher.handlerAdapter.MethodHandlerAdapter;
import org.apache.coyote.http11.dispatcher.handlerAdapter.StaticResourceHandlerAdapter;
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

            HttpRequest httpRequest = new HttpRequest(inputStream);
            DispatcherHandler dispatcherHandler = new DispatcherHandler(List.of(new StaticResourceHandlerAdapter(), new MethodHandlerAdapter()));
            HttpResponse httpResponse = dispatcherHandler.doService(httpRequest);

            outputStream.write(httpResponse.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
