package nextstep.jwp.exception;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.response.HttpResponse;

public class ExceptionHandler {

    private ExceptionHandler() {
    }

    public static void handle(HttpResponse response, HttpStatus httpStatus) {
        response.clearHeadersAndBody();

        if (httpStatus == HttpStatus.BAD_REQUEST) {
            createRedirectResponse(response, "/400.html");
            return;
        }
        if (httpStatus == HttpStatus.UNAUTHORIZED) {
            createRedirectResponse(response, "/401.html");
            response.setDeletingJSessionCookie();
            return;
        }
        if (httpStatus == HttpStatus.NOT_FOUND) {
            createRedirectResponse(response, "/404.html");
            return;
        }

        createRedirectResponse(response, "/500.html");
    }

    private static void createRedirectResponse(HttpResponse response, String location) {
        response.clearHeadersAndBody();

        response.setStatus(HttpStatus.FOUND);
        response.setLocation(location);
    }

}
