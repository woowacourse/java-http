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
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.util.FileExtension;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        ForwardResult result = execute(request, response);

        MimeType mimeType = MimeType.from(FileExtension.HTML);
        response.setMimeType(mimeType);

        if (result.statusCode().isRedirection()) {
            response.setLocation(result.path());
            response.setStatus(result.statusCode());
            response.setBody(new ResponseBody("".getBytes()));
            return;
        }

        try {
            byte[] body = ResourceReader.read(result.path());
            response.setStatus(HttpStatusCode.OK);
            response.setBody(new ResponseBody(Arrays.toString(body).getBytes()));
        } catch (URISyntaxException | IOException e) {
            response.setMimeType(MimeType.OTHER);
            response.setStatus(HttpStatusCode.NOT_FOUND);
            response.setBody(new ResponseBody("No File Found".getBytes()));
        }
    }

    protected abstract ForwardResult execute(HttpRequest request, HttpResponse response);
}
