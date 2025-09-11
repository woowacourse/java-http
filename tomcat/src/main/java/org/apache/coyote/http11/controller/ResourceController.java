package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.MediaType;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(final Http11Request request, final Http11Response response) throws Exception {
        final String path = request.getPath();
        response.readFileFromClasspath("static" + path);
        response.putHeader("Content-Type", MediaType.detectMimeType(path));
    }
}
