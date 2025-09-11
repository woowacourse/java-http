package org.apache.coyote.controller;

import org.apache.coyote.ResourceLoader;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;

public class RootController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        String path = request.getPath();
        byte[] body;

        if (path.contains(".")) {
            body = ResourceLoader.get(path);
        } else {
            body = ResourceLoader.get(path + ".html");
        }

        if (body == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
        } else {
            response.setStatus(HttpStatus.OK);
            response.setBody(body);
        }

        response.setProtocol(request.getProtocol());
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        handleUnsupportedMethod(request, response);
    }
}
