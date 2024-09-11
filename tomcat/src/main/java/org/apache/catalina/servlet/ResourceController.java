package org.apache.catalina.servlet;

import org.apache.catalina.http.HttpRequest;
import org.apache.catalina.http.HttpResponse;
import org.apache.catalina.http.startline.HttpStatus;

public class ResourceController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.isURIStatic()) {
            response.setStatus(HttpStatus.OK);
            responseResource(response, request.getTargetPath());
        }
    }
}
