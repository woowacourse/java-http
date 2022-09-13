package nextstep.jwp.controller;

import static org.apache.coyote.response.ContentType.HTML;

import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.Location;
import org.apache.coyote.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ControllerAdvice.class);
    private static final int STATUS_CODE_INDEX = 0;
    private static final String STATUS_DELIMITER = " ";

    private ControllerAdvice() {
    }

    public static void handle(final HttpResponse response, final String exceptionClassName, final String exceptionMessage) {
        final ExceptionType exceptionType = ExceptionType.from(exceptionClassName);
        final StatusCode statusCode = StatusCode.findSameStatusCode(exceptionType.name());
        final String redirectFileName = getFileName(statusCode);

        log.error("{} : {}", exceptionType.name(), exceptionMessage);
        response.setResponse(statusCode, HTML, Location.from(redirectFileName));
        response.print();
    }

    private static String getFileName(StatusCode statusCode) {
        final String fileName = statusCode.getStatusCode()
                .split(STATUS_DELIMITER)[STATUS_CODE_INDEX];
        return "/" + fileName + ".html";
    }
}
