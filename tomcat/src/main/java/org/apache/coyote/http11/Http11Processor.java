package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpConstants.CONTENT_LENGTH_HEADER;
import static org.apache.coyote.http11.HttpConstants.CONTENT_TYPE_HEADER;
import static org.apache.coyote.http11.HttpConstants.COOKIE_JSESSIONID;
import static org.apache.coyote.http11.HttpConstants.EQUAL;
import static org.apache.coyote.http11.HttpConstants.NOT_FOUND_PAGE;
import static org.apache.coyote.http11.HttpConstants.SERVER_ERROR_PAGE;
import static org.apache.coyote.http11.HttpConstants.SET_COOKIE_HEADER;
import static org.apache.coyote.http11.HttpConstants.SLASH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerResult;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.dto.ContentType;
import org.apache.coyote.http11.dto.request.HttpRequest;
import org.apache.coyote.http11.dto.response.HttpResponse;
import org.apache.coyote.http11.dto.response.ResponseBody;
import org.apache.coyote.http11.dto.response.ResponseHeader;
import org.apache.coyote.http11.dto.response.ResponseLine;
import org.apache.coyote.http11.dto.response.Status;
import org.apache.coyote.http11.parser.request.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final StaticResourceController staticResourceController = new StaticResourceController();
    private static final RequestMapping requestMapping = new RequestMapping(staticResourceController);

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

            HttpRequest request = null;
            try {
                // 1. HTTP 요청 파싱
                request = readRequest(inputStream);

                // 2. 라우팅 및 컨트롤러 실행
                final ControllerResult result = handleRequest(request);
                final HttpResponse response;

                if (result.status() == Status.NOT_FOUND) {
                    // 404 Not Found 처리
                    response = createErrorResponse(request.requestLine().protocolVersion(),
                            Status.NOT_FOUND, SLASH + NOT_FOUND_PAGE);
                } else {
                    // 3. HTTP 응답 생성
                    response = buildHttpResponse(request, result);
                }

                // 4. HTTP 응답 전송
                sendResponse(outputStream, response);

            } catch (final Exception e) {
                log.error("Error processing request", e);

                // 500 Internal Server Error 처리
                final HttpResponse errorResponse = createErrorResponse(request.requestLine().protocolVersion(),
                        Status.INTERNAL_ERROR, SLASH + SERVER_ERROR_PAGE);
                sendResponse(outputStream, errorResponse);
            }
        } catch (final IOException e) {
            log.error("IO error in Http11Processor", e);
        }
    }

    // 1. HTTP 요청 파싱
    private HttpRequest readRequest(final InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return HttpRequestParser.parse(reader);
    }

    // 2. 라우팅 및 컨트롤러 실행
    private ControllerResult handleRequest(final HttpRequest request) throws Exception {
        final Controller controller = requestMapping.getController(request);
        return controller.service(request);
    }

    // 3. HTTP 응답 생성 및 헤더 설정
    private HttpResponse buildHttpResponse(final HttpRequest request, final ControllerResult result) {
        // 응답 라인(Response Line) 생성
        final ResponseLine responseLine = new ResponseLine(request.requestLine().protocolVersion(), result.status());

        // 응답 헤더 생성
        final ResponseHeader responseHeader = new ResponseHeader(result.headers());
        if (result.headers().get(CONTENT_TYPE_HEADER) == null) {
            responseHeader.addHeader(CONTENT_TYPE_HEADER, ContentType.DEFAULT.value());
        }
        responseHeader.addHeader(CONTENT_LENGTH_HEADER,
                String.valueOf(result.body().getBytes(StandardCharsets.UTF_8).length));

        // JSESSIONID 쿠키가 없다면 쿠키 헤더 설정
        if (result.requireSession() && !request.containsCookie(COOKIE_JSESSIONID)) {
            responseHeader.addHeader(SET_COOKIE_HEADER, COOKIE_JSESSIONID + EQUAL + UUID.randomUUID());
        }

        // 응답 바디 생성
        final ResponseBody responseBody = new ResponseBody(result.body());

        return new HttpResponse(responseLine, responseHeader, responseBody);
    }

    // 4. HTTP 응답 전송
    private void sendResponse(final OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }

    private HttpResponse createErrorResponse(
            final String protocolVersion,
            final Status status,
            final String errorPagePath
    ) {
        final ControllerResult result = staticResourceController.serve(errorPagePath);
        final ResponseLine responseLine = new ResponseLine(protocolVersion, status);
        final ResponseHeader responseHeader = new ResponseHeader(result.headers());

        // 명시적으로 Content-Type과 Content-Length 설정
        responseHeader.addHeader(CONTENT_TYPE_HEADER, ContentType.fromPath(errorPagePath).value());
        responseHeader.addHeader(CONTENT_LENGTH_HEADER,
                String.valueOf(result.body().getBytes(StandardCharsets.UTF_8).length));

        final ResponseBody responseBody = new ResponseBody(result.body());
        return new HttpResponse(responseLine, responseHeader, responseBody);
    }
}
