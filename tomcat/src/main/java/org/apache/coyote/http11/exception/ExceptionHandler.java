package org.apache.coyote.http11.exception;

import java.io.InputStream;
import java.util.List;
import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler {

    private static final String NOT_FOUND_PAGE = "static/404.html";
    private static final String INTERNAL_SERVER_ERROR_PAGE = "static/500.html";
    public static final String DEFAULT_ERROR_MESSAGE = "<h1>500 Internal Server Error</h1>";
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    public HttpResponse handle(Exception e) {
        log.error(e.getMessage(), e);

        if (e instanceof NotFoundException) {
            return buildResponse(HttpStatus.NOT_FOUND, NOT_FOUND_PAGE);
        }

        // 404 이외의 예외는 모두 500으로 응답
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_PAGE);
    }

    private HttpResponse buildResponse(HttpStatus status, String pagePath) {
        byte[] content;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(pagePath)) {
            content = is.readAllBytes();
        } catch (Exception e) {
            // IOException, NPE 등 발생 시에도 기본 HTTP 응답을 반환하도록 처리
            log.error(e.getMessage(), e);
            content = DEFAULT_ERROR_MESSAGE.getBytes();
        }

        HttpHeaders headers = HttpHeaders.fromLines(
                List.of(
                        "Content-Type: text/html;charset=utf-8",
                        "Content-Length: " + content.length
                ));
        return new HttpResponse(status, headers, HttpBody.from(content));
    }
}
