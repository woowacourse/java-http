package nextstep.jwp.mapping;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.status.HttpStatus;

public class ExceptionHandler {

    public static void handle(HttpStatus httpStatus, HttpRequest httpRequest,
            HttpResponse httpResponse) {
        String resource = "/" + httpStatus.getValue() + ".html";

        httpResponse.setStatusLine(httpRequest.getProtocolVersion(), httpStatus);
        httpResponse.addResponseHeader("Location", resource);
    }
}
