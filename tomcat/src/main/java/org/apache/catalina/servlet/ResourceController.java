package org.apache.catalina.servlet;

import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;
import org.apache.catalina.http.header.HttpHeader;

public class ResourceController extends Controller {

    private static final String BASIC_RESPONSE_BODY = "Hello world!";

    @Override
    public boolean service(HttpRequest request, HttpResponse response) {
        if (request.isURIBlank()) {
            response.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
            response.setBody(BASIC_RESPONSE_BODY);
            return true;
        }
        if (request.isURIStatic()) {
            return responseResource(response, request.getTargetPath());
        }
        return false;
    }
}
