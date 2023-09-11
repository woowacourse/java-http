package nextstep.jwp.controller;

import nextstep.jwp.AbstractController;
import nextstep.jwp.exception.UnsupportedMethodException;
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

    private String getResourceContent(final String resourcePath) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/" + resourcePath);

        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException("존재하지 않는 파일입니다.");
        }
        return new String(Files.readAllBytes(Path.of(resource.getFile())));
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isGet()) {
            doGet(request, response);
            return;
        }
        throw new UnsupportedMethodException();
    }
}
