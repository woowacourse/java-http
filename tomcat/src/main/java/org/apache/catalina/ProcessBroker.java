package org.apache.catalina;

import org.apache.catalina.handler.HttpHandler;
import org.apache.catalina.handler.HttpHandlerMapper;
import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpResponse;

public class ProcessBroker {

    private final HttpHandlerMapper httpHandlerMapper;

    public ProcessBroker(final HttpHandlerMapper httpHandlerMapper) {
        this.httpHandlerMapper = httpHandlerMapper;
    }

    public void brokeRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        String requestPath = httpRequest.getRequestPath();
        HttpHandler httpHandler = httpHandlerMapper.mappingHandler(requestPath);
        httpHandler.handle(httpRequest, httpResponse);
    }
}
