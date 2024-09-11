package org.apache.coyote.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.util.FileExtension;

public class StaticResourceController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        String resourcePath = "static/" + path;
        try {
            Path filePath = Paths.get(getClass().getClassLoader().getResource(resourcePath).toURI());
            MimeType mimeType = MimeType.from(FileExtension.from(path));
            byte[] body = Files.readAllBytes(filePath);
            ResponseHeader header = new ResponseHeader();
            header.setContentType(mimeType);
//            return new HttpResponse(HttpStatusCode.OK, header, body);
        } catch (URISyntaxException | IOException e) {

            ResponseHeader header = new ResponseHeader();
            header.setContentType(MimeType.OTHER);
//            return new HttpResponse(HttpStatusCode.OK, header, "No File Found".getBytes());
        }
    }
}
