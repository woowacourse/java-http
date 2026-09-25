package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

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
            HttpRequest request = HttpRequestParser.parse(inputStream);
            if (request == null) { // 처리할 요청이 없으면 컨트롤러 선택 및 응답 생성을 건너뜀
                return;
            }
            HttpResponse response = adapter.service(request);
            writeResponse(outputStream, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(final OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }
}
