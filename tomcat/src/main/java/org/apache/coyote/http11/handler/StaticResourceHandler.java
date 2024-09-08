package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import org.apache.coyote.http11.RequestHandler;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Content;
import org.apache.coyote.http11.response.Response;

public class StaticResourceHandler implements RequestHandler {

    @Override
    public String handle(Request request) throws IOException {
        if (request.getMethod() != Method.GET) {
            return null;
        }

        try {
            Content content = getContent(request);
            return Response.writeResponse(request, content.getContentType(), content.getContent());
        } catch (NoSuchFileException e) {
            return null;
        }
    }

    private Content getContent(Request request) throws IOException {
        String target = request.getTarget().equals("/") ? "index.html" : request.getTarget();
        return new Content(target);
    }
}
