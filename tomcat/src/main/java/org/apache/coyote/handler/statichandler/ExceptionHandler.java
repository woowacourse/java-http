package org.apache.coyote.handler.statichandler;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class ExceptionHandler implements Handler {

    private static final String DEFAULT_DIRECTORY_PATH = "static";
    private static final String PATH_DELIMITER = "/";

    private final HttpStatus httpStatus;

    public ExceptionHandler(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        try {
            return HttpResponse.createStaticResponseByPath(
                    httpRequest.httpVersion(),
                    httpStatus,
                    getExceptionPageFilePath()
            );
        } catch (Exception e) {
            return new ExceptionHandler(HttpStatus.NOT_FOUND).handle(httpRequest);
        }
    }

    private String getExceptionPageFilePath() {
        return DEFAULT_DIRECTORY_PATH + PATH_DELIMITER + httpStatus.statusCode() + ContentType.TEXT_HTML.extension();
    }
}
