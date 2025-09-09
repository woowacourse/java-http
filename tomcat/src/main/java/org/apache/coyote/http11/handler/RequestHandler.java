package org.apache.coyote.http11.handler;

import java.net.URL;
import org.apache.coyote.http11.general.ContentType;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;
import org.apache.coyote.http11.httpResponse.StatusLine;

public class RequestHandler {

    private final ApiRouter apiRouter = new ApiRouter();

    public HttpResponse handleHttpRequest(HttpRequest httpRequest) {
        if (httpRequest == null) {
            return new HttpResponse(ContentType.TEXT_HTML, new StatusLine(httpRequest.getProtocolVersion(), HttpStatus.INTERNAL_SERVER_ERROR), "잘못된 요청입니다.");
        }

        if (httpRequest.pathEquals("") || httpRequest.pathEquals("/")) {
            return new HttpResponse(ContentType.TEXT_HTML, new StatusLine(httpRequest.getProtocolVersion(), HttpStatus.OK), "Hello world!");
        }

        URL resourceUrl = getClass().getClassLoader().getResource("static" + httpRequest.getPath());
        if (resourceUrl == null) {
            return apiRouter.route(httpRequest);
        }
        return StaticFileHandler.handle(httpRequest, resourceUrl);
    }
}
