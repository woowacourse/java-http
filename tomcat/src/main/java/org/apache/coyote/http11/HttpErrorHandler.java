package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.exception.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpErrorHandler {

    private static final String CONTENT_TYPE = "text/plain;charset=utf-8 ";
    private static final Logger log = LoggerFactory.getLogger(HttpErrorHandler.class);

    public HttpResponse handle(final String httpVersion, final Exception exception) {
        log.error("HTTP 요청 처리 중 오류가 발생했습니다.", exception);
        if (exception instanceof HttpException httpException) {
            return createErrorResponse(httpVersion, httpException.status().code(), httpException.status().reasonPhrase());
        }
        return createErrorResponse(httpVersion, HttpException.Status.INTERNAL_SERVER_ERROR.code(),
                HttpException.Status.INTERNAL_SERVER_ERROR.reasonPhrase());
    }

    private HttpResponse createErrorResponse(final String httpVersion, final int statusCode,
                                             final String reasonPhrase) {
        final HttpResponse response = new HttpResponse(httpVersion);
        response.setStatus(statusCode, reasonPhrase + " ");
        response.putHeader("Content-Type", CONTENT_TYPE);
        response.setBody(reasonPhrase.getBytes(StandardCharsets.UTF_8));
        return response;
    }
}
