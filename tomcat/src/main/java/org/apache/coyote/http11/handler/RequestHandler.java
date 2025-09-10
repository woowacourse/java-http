package org.apache.coyote.http11.handler;

import java.net.URL;
import org.apache.coyote.http11.general.ContentType;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.coyote.http11.httpResponse.StatusLine;

public class RequestHandler {

    private final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final ApiRouter apiRouter = new ApiRouter();

    public HttpResponse handleHttpRequest(HttpRequest httpRequest) {
        try {
            if (httpRequest == null) {
                return new HttpResponse(ContentType.TEXT_HTML, new StatusLine(httpRequest.getProtocolVersion(), HttpStatus.INTERNAL_SERVER_ERROR), "잘못된 요청입니다.");
            }
            return getResponseFromHandler(httpRequest);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            return new HttpResponse(ContentType.TEXT_HTML, new StatusLine(httpRequest.getProtocolVersion(), HttpStatus.INTERNAL_SERVER_ERROR), "알 수 없는 오류가 발생했습니다.");
        }
    }

    private HttpResponse getResponseFromHandler(HttpRequest httpRequest) {
        if (httpRequest.pathEquals("") || httpRequest.pathEquals("/")) {
            return new HttpResponse(ContentType.TEXT_HTML, new StatusLine(httpRequest.getProtocolVersion(), HttpStatus.OK), "Hello world!");
        }

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
