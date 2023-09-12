package nextstep.jwp.controller;

import nextstep.jwp.AbstractController;
import nextstep.jwp.exception.ResourceNotFoundException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.HttpStatus;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ResourceController extends AbstractController {

    private String getResourceContent(final String resourcePath) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/" + resourcePath);
        if (Objects.isNull(resource)) {
            if (resourcePath.endsWith(".html")) {
                return getResourceContent("/404.html");
            }
            throw new ResourceNotFoundException(resourcePath);
        }
        return new String(Files.readAllBytes(Path.of(resource.getFile())));
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String resourcePath = request.getRequestLine().getUri().getPath();
        final String extension = resourcePath.substring(resourcePath.lastIndexOf(".") + 1);
        final ContentType contentType = ContentType.from(extension);
        final String content = getResourceContent(resourcePath);
        response.addStatus(HttpStatus.OK)
                .addContentType(contentType)
                .addBody(content);
    }
}
