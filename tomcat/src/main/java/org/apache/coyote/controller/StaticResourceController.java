package org.apache.coyote.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.MimeType;
import org.apache.coyote.file.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.util.FileExtension;

public class StaticResourceController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        try {
            ResponseHeader header = new ResponseHeader();
            header.setContentType(MimeType.from(FileExtension.from(path)));
            byte[] body = ResourceReader.read(path);
            response.setBody(new ResponseBody(body));
//            return new HttpResponse(HttpStatusCode.OK, header, body);
        } catch (URISyntaxException | IOException e) {
            ResponseHeader header = new ResponseHeader();
            header.setContentType(MimeType.OTHER);
            response.setBody(new ResponseBody("".getBytes()));
//            return new HttpResponse(HttpStatusCode.OK, header, "No File Found".getBytes());
        }
    }
}
