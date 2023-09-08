package nextstep.jwp.exception;

import nextstep.jwp.http.common.HeaderType;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.response.HttpResponse;

public class ExceptionHandler {

    private ExceptionHandler() {
    }

    public static void handle(HttpResponse response, HttpStatus httpStatus) {
        if (httpStatus == HttpStatus.BAD_REQUEST) {
            createRedirectResponse(response, "/400.html");
            return;
        }
        if (httpStatus == HttpStatus.UNAUTHORIZED) {
            createRedirectResponse(response, "/401.html");
            return;
        }
        if (httpStatus == HttpStatus.NOT_FOUND) {
            createRedirectResponse(response, "/404.html");
            return;
        }

        createRedirectResponse(response, "/500.html");
    }

    private static HttpResponse createRedirectResponse(HttpResponse response, String location) {
        HttpResponse.createEmptyResponse();
        response.setStatus(HttpStatus.FOUND);
        response.setHeader(HeaderType.LOCATION.getValue(), location);

        return response;
    }

}
