package org.apache.coyote.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.apache.coyote.ForwardResult;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.util.FileExtension;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        ForwardResult result = execute(request, response);

        ResponseHeader header = result.header();
        MimeType mimeType = MimeType.from(FileExtension.HTML);
        header.setContentType(mimeType);

        if (result.statusCode().isRedirection()) {
            header.setLocation(result.path());
            response.setStatus(result.statusCode());
            response.setHeader(header);
            response.setBody(new ResponseBody("".getBytes()));
            return;
        }

        try {
            Path filePath = Paths.get(getClass().getClassLoader().getResource("static/" + result.path()).toURI());
            byte[] body = Files.readAllBytes(filePath);
            response.setStatus(HttpStatusCode.OK);
            response.setHeader(header);
            response.setBody(new ResponseBody(Arrays.toString(body).getBytes()));
        } catch (URISyntaxException | IOException e) {
            header.setContentType(MimeType.OTHER);
            response.setStatus(HttpStatusCode.NOT_FOUND);
            response.setHeader(header);
            response.setBody(new ResponseBody("No File Found".getBytes()));
        }
    }

    protected abstract ForwardResult execute(HttpRequest request, HttpResponse response);
}
