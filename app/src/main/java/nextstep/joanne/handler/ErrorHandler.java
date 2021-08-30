package nextstep.joanne.handler;

import nextstep.joanne.http.HttpStatus;
import nextstep.joanne.http.response.HttpResponse;

public class ErrorHandler {
    public static HttpResponse handle(HttpStatus httpStatus) {
        return mappedByStatusCode(httpStatus);
    }

    private static HttpResponse mappedByStatusCode(HttpStatus httpStatus) {
        if (httpStatus.equals(HttpStatus.UNAUTHORIZED)) {
            return ResourceTemplate.doPage(HttpStatus.UNAUTHORIZED, "/401.html");
        }
        if (httpStatus.equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            return ResourceTemplate.doPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
        }
        if (httpStatus.equals(HttpStatus.NOT_FOUND)) {
            return ResourceTemplate.doPage(HttpStatus.NOT_FOUND, "/404.html");
        }
        return ResourceTemplate.doPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
    }
}
