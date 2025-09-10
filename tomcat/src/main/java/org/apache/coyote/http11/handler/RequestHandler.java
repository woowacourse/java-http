package org.apache.coyote.http11.handler;

import java.net.URL;
import org.apache.coyote.http11.general.ContentType;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public class RequestHandler {

    private final ApiRouter apiRouter = new ApiRouter();

    public HttpResponse handleHttpRequest(HttpRequest httpRequest) {
        if (httpRequest == null) {
            return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.TEXT_HTML, "잘못된 요청입니다.");
        }

        if (httpRequest.pathEquals("") || httpRequest.pathEquals("/")) {
            return new HttpResponse(HttpStatus.OK, ContentType.TEXT_HTML, "Hello world!");
        }

        return getResponseFromHandler(httpRequest);
    }

    private HttpResponse getResponseFromHandler(HttpRequest httpRequest) {
        URL resourceUrl = getClass().getClassLoader().getResource("static" + httpRequest.getPath());
        if (isStaticFileRequest(resourceUrl)) {
            return StaticFileHandler.handle(httpRequest, resourceUrl);
        }
        return apiRouter.route(httpRequest);
    }

    private boolean isStaticFileRequest(URL resourceUrl) {
        return resourceUrl != null;
    }
}
