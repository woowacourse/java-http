package nextstep.jwp.presentation;

import static org.apache.coyote.http11.ContentType.TEXT_HTML;

import nextstep.jwp.common.ResourceLoader;
import nextstep.jwp.exception.BaseException;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class ExceptionControllerAdvice {

    private static final String RESOURCE_PATH_FORMAT = "static/%s.html";

    void handleException(BaseException e, HttpResponse response) {
        HttpStatus httpStatus = e.exceptionType().httpStatus();
        response.setStatus(httpStatus);
        int code = httpStatus.statusCode();
        String body = ResourceLoader.load(String.format(RESOURCE_PATH_FORMAT, code));
        response.setBody(body);
        response.setHeader("Content-Type", TEXT_HTML.value());
    }
}
