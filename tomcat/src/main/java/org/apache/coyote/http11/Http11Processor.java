package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestGenerator;
import org.apache.coyote.common.Request;
import org.apache.coyote.common.Response;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.HandlerMapper;
import org.apache.coyote.handler.StaticResourceHandler;
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
             final var outputStream = connection.getOutputStream();
             final var requestReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final var request = RequestGenerator.accept(requestReader);
            final var response = getResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Response getResponse(Request request) throws IOException {
        Handler handler = HandlerMapper.findHandler(request.getMethod(), request.getUri());
        if (handler != null) {
            return handler.handle(request);
        }
        // todo: GET 메서드가 아닌 경우 405 Method Not Allowed 응답을 반환
        return StaticResourceHandler.getInstance().handle(request);
    }
}
