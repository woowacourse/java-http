package org.apache.coyote.util;

import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class ResourceHandler {

    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        String path = httpRequest.getPath();
        try {
            httpResponse.setResponseBody(FileReader.read(path));
        } catch (IOException | URISyntaxException exception) {
            httpResponse.setResponseBody("".getBytes(StandardCharsets.UTF_8));
        }

        httpResponse.setMimeType(MimeType.from(path));
        httpResponse.setStatus(HttpStatus.OK);
    }
}
