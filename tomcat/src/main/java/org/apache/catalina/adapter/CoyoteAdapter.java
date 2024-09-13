package org.apache.catalina.adapter;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.servlet.ExceptionHandlerMapping;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;
import org.apache.tomcat.http.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoyoteAdapter {

    private static final Logger log = LoggerFactory.getLogger(CoyoteAdapter.class);

    private final RequestMapping requestMapping;
    private final ExceptionHandlerMapping handlerMapping;

    public CoyoteAdapter() {
        this.requestMapping = new RequestMapping();
        this.handlerMapping = new ExceptionHandlerMapping();
    }

    public Response service(final Request request) {
        final var httpRequest = new HttpRequest(request.getRequestLine(), request.getRequestHeaders(),
                request.getBody(), request.getCookie());
        final var httpResponse = new HttpResponse(StatusCode.NOT_FOUND);
        handle(httpRequest, httpResponse);
        return new Response(httpResponse.getStatusLine(), httpResponse.getResponseHeader(), httpResponse.getBody());
    }

    private void handle(final HttpRequest request, final HttpResponse response) {
        try {
            final var controller = requestMapping.get(request.getUriPath());
            controller.service(request, response);
        } catch (Exception exception) {
            log.warn("[KNOWN ERROR] : {}", exception.getMessage());
            final var handler = handlerMapping.get(exception.getClass());
            final var result = handler.handle();
            response.copyProperties(result);
        }
    }
}
