package org.apache.coyote.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.catalina.Manager;
import org.apache.coyote.ForwardResult;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.util.FileExtension;

public abstract class AbstractController implements Controller {

    public HttpResponse service(HttpRequest request, Manager manager) {
        ForwardResult result = execute(request, manager);

        ResponseHeader header = result.header();
        MimeType mimeType = MimeType.from(FileExtension.HTML);
        header.setContentType(mimeType);

        if (result.statusCode().isRedirection()) {
            header.setLocation(result.path());
            return new HttpResponse(result.statusCode(), header, new byte[]{});
        }

        try {
            Path filePath = Paths.get(getClass().getClassLoader().getResource("static/" + result.path()).toURI());
            byte[] body = Files.readAllBytes(filePath);
            return new HttpResponse(HttpStatusCode.OK, header, body);
        } catch (URISyntaxException | IOException e) {
            header.setContentType(MimeType.OTHER);
            return new HttpResponse(HttpStatusCode.NOT_FOUND, header, "No File Found".getBytes());
        }
    }

    protected abstract ForwardResult execute(HttpRequest request, Manager manager);
}
