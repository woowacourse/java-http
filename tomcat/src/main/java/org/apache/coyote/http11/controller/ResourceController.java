package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.MediaType;
import org.apache.coyote.http11.exception.Http11ParseException;

public class ResourceController extends AbstractController {

    @Override
    protected void doGet(final Http11Request request, final Http11Response response) throws Http11ParseException {
        final String path = request.getPath();
        final String resource = response.readFileFromClasspath("static" + path);

        if (resource.isEmpty()) {
            response.putStatusLine("HTTP/1.1 404 Not Found");
            response.putHeader("Content-Type", "text/html; charset=utf-8");
            response.putBody(response.readFileFromClasspath("static/404.html"));
            return;
        }

        response.putHeader("Content-Type", MediaType.detectMimeType(path));
        response.putBody(resource);
    }
}
