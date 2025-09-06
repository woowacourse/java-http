package org.apache.coyote.http11.handler;

import java.net.URL;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;

public class RequestHandler {

    private final ApiRouter apiRouter = new ApiRouter();

    public HttpResponse handleHttpRequest(HttpRequest httpRequest) {
        if (httpRequest == null) {
            return new HttpResponse("500 Internal Server Error", "text/html;charset=utf-8", null);
        }

        if (httpRequest.pathEquals("") || httpRequest.pathEquals("/")) {
            return new HttpResponse("200 OK", "text/html;charset=utf-8", "Hello world!");
        }

        URL resourceUrl = getClass().getClassLoader().getResource("static" + httpRequest.getPath());
        if (resourceUrl == null) {
            return apiRouter.route(httpRequest);
        }
        return StaticFileHandler.handle(httpRequest, resourceUrl);
    }
}
