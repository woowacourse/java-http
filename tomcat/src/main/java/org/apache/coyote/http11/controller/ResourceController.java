package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Status;
import org.apache.coyote.http11.MediaType;
import org.apache.coyote.http11.exception.Http11ParseException;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(final Http11Request request, final Http11Response response) throws Http11ParseException {
        final String path = request.getPath();
        final String resource = response.readFileFromClasspath("static" + path);

        if (resource.isEmpty()) {
            response.sendError(Http11Status.NOT_FOUND);
            return;
        }
        response.putHeader("Content-Type", MediaType.detectMimeType(path));
        response.putBody(resource);
    }
}
