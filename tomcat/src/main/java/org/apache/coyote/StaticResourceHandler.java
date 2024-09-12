package org.apache.coyote;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.file.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.util.FileExtension;

public class StaticResourceHandler {

    public void handle(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        try {
            response.setBody(ResourceReader.read(path));

        } catch (URISyntaxException | IOException e) {
            response.setBody("".getBytes());
        }
        response.setMimeType(MimeType.from(FileExtension.from(path)));
        response.setStatus(HttpStatusCode.OK);
    }
}
