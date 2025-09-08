package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.util.HttpRequestParser;
import org.apache.coyote.http11.helper.Responses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Handler dispatcher;

    public Http11Processor(Socket connection, Handler dispatcher) {
        this.connection = connection;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = HttpRequestParser.parse(inputStream);
            dispatcher.handle(httpRequest, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
            try {
                Responses.serverError(connection.getOutputStream(), "HTTP/1.1");
            } catch (IOException ioException) {
                log.error("서버 에러 응답 전송 실패: {}", ioException.getMessage(), ioException);
            }
        }
    }
}
