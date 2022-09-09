package org.apache.coyote.http11.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

public class FileController extends AbstractController {

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new IllegalArgumentException("처리할 수 없는 요청입니다.");
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        URL resource = getClass().getClassLoader().getResource("static" + httpRequest.getPath());

        if (resource == null) {
            httpResponse.notFound();
            return;
        }

        File file = new File(resource.getFile());
        Path path = file.toPath();
        String responseBody = new String(Files.readAllBytes(path));
        ContentType contentType = ContentType.of(Files.probeContentType(path));

        httpResponse.ok(contentType, responseBody);
    }
}
