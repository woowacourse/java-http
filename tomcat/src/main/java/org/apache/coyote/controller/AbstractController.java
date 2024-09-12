package org.apache.coyote.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.apache.coyote.ForwardResult;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.MimeType;
import org.apache.coyote.file.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.util.FileExtension;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        ForwardResult result = execute(request, response);

        MimeType mimeType = MimeType.from(FileExtension.from(request.getPath()));
        response.setMimeType(mimeType);

        if (result.isRedirection()) {
            response.setLocation(result.path());
            response.setStatus(result.statusCode());
            response.setBody("".getBytes());
            return;
        }

        try {
            byte[] body = ResourceReader.read(result.path());
            response.setStatus(HttpStatusCode.OK);
            response.setBody(Arrays.toString(body).getBytes());
        } catch (URISyntaxException | IOException e) {
            response.setMimeType(MimeType.OTHER);
            response.setStatus(HttpStatusCode.NOT_FOUND);
            response.setBody("No File Found".getBytes());
        }
    }

    protected abstract ForwardResult execute(HttpRequest request, HttpResponse response);
}
