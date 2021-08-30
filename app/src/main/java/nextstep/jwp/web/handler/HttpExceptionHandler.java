package nextstep.jwp.web.handler;

import nextstep.jwp.web.http.response.ContentType;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.StatusCode;

public class HttpExceptionHandler {

    public void handle(StatusCode statusCode, HttpResponse response) {
        String targetResource = "/" + statusCode.getCode() + ContentType.HTML.getExtension();

        response.setStatusLine(StatusCode.FOUND);
        response.addHeader("Location", targetResource);
    }
}
