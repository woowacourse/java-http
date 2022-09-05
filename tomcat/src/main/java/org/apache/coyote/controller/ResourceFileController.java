package org.apache.coyote.controller;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.httpmessage.common.ContentType;
import org.apache.coyote.http11.httpmessage.request.Request;
import org.apache.coyote.http11.httpmessage.response.Response;

public class ResourceFileController extends AbstractController {
    @Override
    public void service(Request request, Response response) throws Exception {
        if (request.isGetMethod()) {
            doGet(request, response);
        }
    }

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        final String fileName = request.getUri().getResourcePath();
        final String fileExtension = fileName.split("\\.")[1];

        final ContentType contentType = ContentType.from(fileExtension);
        final URL resource = getClass().getClassLoader().getResource("static" + fileName);
        final Path path = new File(resource.getPath()).toPath();

        final String responseBody = new String(Files.readAllBytes(path));

        response.ok(responseBody)
                .addHeader("Content-Type", contentType.getContentType() + ";charset=utf-8")
                .addHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
    }
}
