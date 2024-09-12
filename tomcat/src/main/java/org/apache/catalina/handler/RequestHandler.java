package org.apache.catalina.handler;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.exception.FileException;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final RequestMapping requestMapping;

    public RequestHandler(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        try {
            return createResponse(request);
        } catch (FileException e) {
            log.error(e.getMessage(), e);
            return new HttpResponse(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpResponse createResponse(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        Controller controller = requestMapping.getController(request);
        controller.service(request, response);

        return response;
    }
}
