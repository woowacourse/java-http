package org.apache.coyote.http11.request;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.MimeType;

public class ResourceRequestProcessor implements HttpRequestProcessor {

    @Override
    public HttpResponse process(HttpRequest request) {
        String requestURI = request.getRequestURI();
        File resource = ResourceLocator.findResource(requestURI.substring(requestURI.lastIndexOf("/")));
        String extension = requestURI.substring(requestURI.lastIndexOf(".") + 1);

        String responseBody = null;
        try {
            responseBody = new String(Files.readAllBytes(resource.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new HttpResponse(
                HttpStatus.OK,
                MimeType.of(extension),
                responseBody);
    }
}
