package org.apache.coyote.controller;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.controller.utils.PathFinder;
import org.apache.coyote.http11.httpmessage.common.ContentType;
import org.apache.coyote.http11.httpmessage.request.Request;
import org.apache.coyote.http11.httpmessage.response.Response;

public class ResourceFileController extends AbstractController {

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        final String fileName = request.getUri().getResourcePath();
        final Path path = PathFinder.findByFileName(fileName);

        final String fileExtension = fileName.split("\\.")[1];
        final ContentType contentType = ContentType.from(fileExtension);

        final String responseBody = new String(Files.readAllBytes(path));

        response.ok(responseBody)
                .addHeader("Content-Type", contentType.getContentType() + ";charset=utf-8")
                .addHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
    }
}
