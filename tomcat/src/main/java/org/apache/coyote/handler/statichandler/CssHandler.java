package org.apache.coyote.handler.statichandler;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.http11.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CssHandler implements Handler {

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource("static" + httpRequest.uri()).getPath());
            byte[] bytes = Files.readAllBytes(path);
            String body = new String(bytes);
            HttpResponse response = new HttpResponse();
            response.setHttpVersion(HttpVersion.HTTP11.value());
            response.setHttpStatus(HttpStatus.OK);
            response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.TEXT_CSS.value())
                    .setHeader(HttpHeader.CONTENT_LENGTH, body.getBytes().length + " ");
            response.setBody(body);
            return response;
        } catch (IOException e) {
        }
        return null;
    }
}
