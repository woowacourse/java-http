package nextstep.jwp.presentation;

import static org.apache.coyote.http11.ContentType.TEXT_HTML;

import nextstep.jwp.common.ResourceLoader;
import nextstep.jwp.exception.BaseException;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class ExceptionControllerAdvice {

    private static final String RESOURCE_PATH_FORMAT = "static/%s.html";

    HttpResponse handleException(BaseException e) {
        HttpStatus httpStatus = e.exceptionType().httpStatus();
        int code = httpStatus.statusCode();
        ResourceLoader resourceLoader = new ResourceLoader();
        String body = resourceLoader.load(String.format(RESOURCE_PATH_FORMAT, code));
        return HttpResponse.status(httpStatus)
                .contentType(TEXT_HTML)
                .body(body)
                .build();
    }
}
