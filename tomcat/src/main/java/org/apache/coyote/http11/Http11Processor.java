package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpConstants.CONTENT_LENGTH_HEADER;
import static org.apache.coyote.http11.HttpConstants.CONTENT_TYPE_HEADER;
import static org.apache.coyote.http11.HttpConstants.COOKIE_JSESSIONID;
import static org.apache.coyote.http11.HttpConstants.DEFAULT_PROTOCOL;
import static org.apache.coyote.http11.HttpConstants.EQUAL;
import static org.apache.coyote.http11.HttpConstants.SET_COOKIE_HEADER;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
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
    private static final StaticFileHandler STATIC_FILE_HANDLER = new StaticFileHandler();
    private static final Router ROUTER = new Router(STATIC_FILE_HANDLER);

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
            // 1. HTTP 요청 파싱
            final HttpRequest request = readRequest(inputStream);

            // 2. 라우팅 및 핸들러 실행
            final HandlerResult result = handleRequest(request);

            // 3. HTTP 응답 생성 및 전송
            sendResponse(outputStream, request, result);
        } catch (final IllegalArgumentException e) {
            log.warn(e.getMessage());
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    // 1. HTTP 요청 파싱
    private HttpRequest readRequest(final InputStream inputStream) throws IOException {
        final Optional<HttpRequest> optionalRequest = HttpRequestParser.parse(inputStream);
        if (optionalRequest.isEmpty()) {
            throw new IllegalArgumentException("Invalid or empty HTTP request");
        }
        return optionalRequest.get();
    }

    // 2. 라우팅 및 핸들러 실행
    private HandlerResult handleRequest(final HttpRequest request) {
        final Handler handler = ROUTER.route(request);
        return handler.doHandle(request);
    }

    // 3. HTTP 응답 생성 및 전송
    private void sendResponse(final OutputStream outputStream, final HttpRequest request, final HandlerResult result)
            throws IOException {
        final HttpResponse response = buildHttpResponse(request, result);
        outputStream.write(response.toBytes());
        outputStream.write(result.body());
        outputStream.flush();
    }

    // 3.1 HTTP 응답 생성 및 헤더 설정
    private HttpResponse buildHttpResponse(final HttpRequest request, final HandlerResult result) {
        final HttpResponse response = new HttpResponse(DEFAULT_PROTOCOL, result.status(), new LinkedHashMap<>());
        response.addHeader(CONTENT_TYPE_HEADER, result.contentType().value());
        response.addHeader(CONTENT_LENGTH_HEADER, String.valueOf(result.body().length));

        // 핸들러별 추가 헤더 설정 (e.g. LoginHandler의 302 Location)
        for (final Entry<String, String> header : result.headers().entrySet()) {
            response.addHeader(header.getKey(), header.getValue());
        }

        // JSESSIONID 쿠키가 없다면 쿠키 헤더 설정
        if (result.requiresSession() && !request.httpCookie().containsCookie(COOKIE_JSESSIONID)) {
            response.addHeader(SET_COOKIE_HEADER, COOKIE_JSESSIONID + EQUAL + UUID.randomUUID());
        }
        return response;
    }
}
