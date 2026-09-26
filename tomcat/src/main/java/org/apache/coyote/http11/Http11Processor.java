package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.request.BadRequestException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;
    private final Adapter adapter;

    public Http11Processor(final Socket connection, final Adapter adapter) {
        this.connection = connection;
        this.adapter = adapter;
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
            final Optional<HttpRequest> request = parseRequest(inputStream, outputStream);
            if (request.isEmpty()) { // 처리할 요청이 없으면 컨트롤러 선택 및 응답 생성을 건너뜀
                return;
            }
            final HttpResponse response = adapter.service(request.get());
            writeResponse(outputStream, response);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private Optional<HttpRequest> parseRequest(final InputStream inputStream,
                                              final OutputStream outputStream) throws IOException {
        try {
            return Optional.ofNullable(HttpRequestParser.parse(inputStream));
        } catch (final BadRequestException e) {
            log.warn("잘못된 HTTP 요청입니다: {}", e.getMessage());
            writeResponse(outputStream, HttpResponse.empty(HttpStatus.BAD_REQUEST));
            return Optional.empty();
        }
    }

    private void writeResponse(final OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }
}
