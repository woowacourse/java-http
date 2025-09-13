package org.apache.coyote.http11;

import org.apache.catalina.mapper.RequestMapper;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.util.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapper requestMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapper = new RequestMapper();
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
            HttpResponse response = HttpResponse.empty();

            final var controller = requestMapper.getController(request);
            controller.service(request, response);
            response.send(outputStream);
        } catch (Exception e) {
            // TODO: exception class에 따른 분기 - 에러 상황에 맞는 정적 파일 보내기
            log.error(e.getMessage(), e);
        }
    }
}
