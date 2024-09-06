package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import org.apache.coyote.http11.RequestHandler;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Content;

public class StaticResourceHandler implements RequestHandler {

    @Override
    public String handle(Request request) throws IOException {
        if (request.getMethod() != Method.GET) {
            return null;
        }

        try {
            Content content = getContent(request);
            return String.join("\r\n",
                    String.format("%s 200 OK ", request.getHttpVersion()),
                    String.format("Content-Type: %s;charset=utf-8 ", content.getContentType()),
                    "Content-Length: " + content.getContent().getBytes().length + " ",
                    "",
                    content.getContent());
        } catch (NoSuchFileException e) {
            return null;
        }
    }

    private Content getContent(Request request) throws IOException {
        String target = request.getTarget().equals("/") ? "index.html" : request.getTarget();
        return new Content(target);
    }
}
