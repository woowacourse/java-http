package org.apache.coyote.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.exception.ExceptionPage;
import org.apache.coyote.response.HttpResponse.HttpResponseBuilder;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.exception.HttpException;

public class ResourceView {

    public HttpResponse findStaticResource(String uri) {
        final var path = toResourcePath(uri);
        return toHttpResponse(HttpStatus.OK, path);
    }

    public HttpResponse findErrorPage(HttpException exception) {
        final var status = exception.getStatus();
        final var path = toResourcePath(ExceptionPage.toUri(status));
        return toHttpResponse(status, path);
    }

    private Path toResourcePath(String uri) {
        try {
            final var classLoader = getClass().getClassLoader();
            final var url = classLoader.getResource("static" + toDefaultUri(uri));
            final var file = new File(url.getFile());
            return file.toPath();
        } catch (NullPointerException e) {
            throw HttpException.ofNotFound(e);
        }
    }

    private static String toDefaultUri(String uri) {
        if (uri.equals("/")) {
            return "/index.html";
        }
        if (!uri.contains(".")) {
            return uri + ".html";
        }
        return uri;
    }

    private HttpResponse toHttpResponse(HttpStatus status, Path path) {
        try {
            return new HttpResponseBuilder(status)
                    .setContentType(Files.probeContentType(path))
                    .setMessageBody(new String(Files.readAllBytes(path)))
                    .build();
        } catch (IOException e) {
            throw HttpException.ofInternalServerError(e);
        }
    }
}
