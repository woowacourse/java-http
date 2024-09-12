package org.apache.coyote;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.file.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.util.FileExtension;

public class StaticResourceHandler {

    public void service(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        try {
            byte[] body = ResourceReader.read(path);
            response.setBody(new ResponseBody(body));
            response.setMimeType(MimeType.from(FileExtension.from(path)));
            response.setStatus(HttpStatusCode.OK);
        } catch (URISyntaxException | IOException e) {
            response.setBody(new ResponseBody("".getBytes()));
            response.setMimeType(MimeType.from(FileExtension.from(path)));
            response.setStatus(HttpStatusCode.OK);
        }
    }
}
