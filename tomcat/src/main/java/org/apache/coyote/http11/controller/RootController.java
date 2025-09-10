package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.ResourceLoader;

public class RootController extends AbstractController {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        super.service(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        try {
            String path = request.getUri().getPath();
            response.setStatusCode(HttpStatusCode.OK);
            response.write(ResourceLoader.findResource(path));
            response.setMimeType(ResourceLoader.getMimeType(path));
            response.send();
        } catch (Exception e) {
            response.setStatusCode(HttpStatusCode.FOUND);
            response.sendRedirect("/401.html");
        }
    }
}
