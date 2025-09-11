package org.apache.coyote.controller;

import java.util.Objects;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.MediaTypes;
import org.apache.coyote.util.ResourceLoader;

public class RootController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        String path = request.getPath();
        byte[] body;

        if (path.contains(".")) {
            body = ResourceLoader.get(path);
            response.setHeader("Content-Type", MediaTypes.findMediaType(path));
        } else {
            body = ResourceLoader.get(path + ".html");
            response.setHeader("Content-Type", MediaTypes.findMediaType(path + ".html"));
        }
        if (Objects.equals(path, "/")) {
            body = ResourceLoader.get("/index.html");
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
