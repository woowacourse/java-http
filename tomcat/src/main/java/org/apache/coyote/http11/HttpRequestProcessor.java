package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class HttpRequestProcessor {

    public HttpResponse process(HttpRequest request) {
        String requestURI = request.getRequestURI();
        if (isRoot(requestURI)) {
            String responseBody = "Hello world!";
            return new HttpResponse(
                    "HTTP/1.1 200 OK",
                    "Content-Type: text/html;charset=utf-8\r\nContent-Length: " + responseBody.getBytes().length,
                    responseBody);
        }

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

    private boolean isRoot(String requestURI) {
        return "/".equals(requestURI);
    }
}
