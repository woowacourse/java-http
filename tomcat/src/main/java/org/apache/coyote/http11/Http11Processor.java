package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.dto.HttpResponse;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.HandlerResult;
import org.apache.coyote.http11.handler.StaticFileHandler;
import org.apache.coyote.http11.parser.HttpRequestParser;
import org.apache.coyote.http11.router.Router;
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
             final var outputStream = connection.getOutputStream()
        ) {
            // 1. 요청 라인(Request Line) 파싱
            final Optional<HttpRequest> optionalRequest = HttpRequestParser.parse(inputStream);
            if (optionalRequest.isEmpty()) {
                return;
            }
            final HttpRequest request = optionalRequest.get();

            // 2. 라우팅 및 핸들러 실행
            final Router router = new Router(new StaticFileHandler());
            final Handler handler = router.route(request);
            final HandlerResult result = handler.doHandle(request);

            // 3. HTTP 응답 생성 및 헤더 설정
            final HttpResponse response = new HttpResponse("HTTP/1.1", result.status(), new LinkedHashMap<>());
            response.addHeader("Content-Type", result.contentType());
            response.addHeader("Content-Length", String.valueOf(result.body().length));

            // 4. 응답 전송
            outputStream.write(response.toBytes());
            outputStream.write(result.body());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
