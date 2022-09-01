package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ResourceRequestProcessor implements HttpRequestProcessor {

    @Override
    public HttpResponse process(HttpRequest request) {
        String requestURI = request.getRequestURI();
        ResourceLocator resourceLocator = new ResourceLocator();
        File resource = resourceLocator.findResource(requestURI.substring(requestURI.lastIndexOf("/")));
        String extension = requestURI.substring(requestURI.lastIndexOf(".") + 1);

        String responseBody = null;
        try {
            responseBody = new String(Files.readAllBytes(resource.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new HttpResponse(
                "HTTP/1.1 200 OK",
                "Content-Type: text/"+extension+";charset=utf-8\r\nContent-Length: " + responseBody.getBytes().length,
                responseBody);
    }
}
