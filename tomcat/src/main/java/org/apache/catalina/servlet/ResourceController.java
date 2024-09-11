package org.apache.catalina.servlet;

import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;
import org.apache.catalina.http.header.HttpHeader;
import org.apache.catalina.http.startline.HttpStatus;

public class ResourceController extends AbstractController {

    private static final String BASIC_RESPONSE_BODY = "Hello world!";

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.isURIHome()) {
            response.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
            response.setBody(BASIC_RESPONSE_BODY);
            response.setStatus(HttpStatus.OK);
        }
        if (request.isURIStatic()) {
            response.setStatus(HttpStatus.OK);
            responseResource(response, request.getURI());
        }
    }
}
