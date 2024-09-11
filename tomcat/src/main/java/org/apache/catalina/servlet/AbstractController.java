package org.apache.catalina.servlet;

import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;
import org.apache.catalina.http.header.HttpHeader;
import org.apache.catalina.http.startline.HttpStatus;
import org.apache.catalina.util.ResourceReader;

public abstract class AbstractController implements Controller {

    private static final String UTF_8_ENCODING = ";charset=utf-8";

    @Override
    public abstract void service(HttpRequest request, HttpResponse response);

    protected void redirectTo(HttpResponse response, String target) {
        response.setStatus(HttpStatus.FOUND);
        response.addHeader(HttpHeader.LOCATION, target);
    }

    protected void responseResource(HttpResponse response, String path) {
        String responseBody = ResourceReader.read(path);
        String contentType = ResourceReader.probeContentType(path);
        response.addHeader(HttpHeader.CONTENT_TYPE, contentType + UTF_8_ENCODING);
        response.setBody(responseBody);
    }
}
