package nextstep.jwp.exception;

import nextstep.jwp.http.HttpBody;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpStatusLine;

public class ExceptionHandler {

    private ExceptionHandler() {
    }

    public static HttpResponse handle(HttpRequest request, HttpStatus httpStatus) {
        if (httpStatus == HttpStatus.BAD_REQUEST) {
            return createRedirectResponse(request, "/400.html");
        }
        if (httpStatus == HttpStatus.UNAUTHORIZED) {
            return createRedirectResponse(request, "/401.html");
        }
        if (httpStatus == HttpStatus.NOT_FOUND) {
            return createRedirectResponse(request, "/404.html");
        }

        return createRedirectResponse(request, "/500.html");
    }

    private static HttpResponse createRedirectResponse(HttpRequest request, String location) {
        HttpStatusLine httpStatusLine = new HttpStatusLine(request.getHttpVersion(), HttpStatus.FOUND);
        HttpBody httpBody = HttpBody.createEmptyBody();
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);
        httpHeaders.setLocation(location);

        return new HttpResponse(httpStatusLine, httpHeaders, httpBody);
    }

}
