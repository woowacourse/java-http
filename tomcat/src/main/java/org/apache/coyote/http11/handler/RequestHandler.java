package org.apache.coyote.http11.handler;

import java.net.URL;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public class RequestHandler {

    private final ApiRouter apiRouter = new ApiRouter();

    public HttpResponse handleHttpRequest(HttpRequest httpRequest) {
        if (httpRequest == null) {
            return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, "text/html;charset=utf-8", null);
        }

        if (httpRequest.pathEquals("") || httpRequest.pathEquals("/")) {
            return new HttpResponse(HttpStatus.OK, "text/html;charset=utf-8", "Hello world!");
        }

        URL resourceUrl = getClass().getClassLoader().getResource("static" + httpRequest.getPath());
        if (resourceUrl == null) {
            return apiRouter.route(httpRequest);
        }
        return StaticFileHandler.handle(httpRequest, resourceUrl);
    }
}
