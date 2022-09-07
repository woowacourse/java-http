package org.apache.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.util.PathUtils;

public class FileController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        final String url = request.getUrl();
        final Path path = PathUtils.load(url);

        final String extension = url.split("\\.")[1];
        final String responseBody = new String(Files.readAllBytes(path));
        final ContentType contentType = ContentType.findContentType(extension);

        return new HttpResponse(HttpStatus.OK, contentType, responseBody);
    }
}
