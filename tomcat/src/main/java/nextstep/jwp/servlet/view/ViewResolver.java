package nextstep.jwp.servlet.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.servlet.response.ResponseEntity;
import org.apache.coyote.support.HttpException;

public class ViewResolver {

    public HttpResponse findStaticResource(String uri) {
        final var path = toResourcePath(uri);
        return ResponseEntity.ok()
                .setContentType(findContentType(path))
                .setMessageBody(findContent(path))
                .build()
                .toHttpResponse();
    }

    public HttpResponse findStaticResource(ViewResource viewResource) {
        final var status = viewResource.getStatus();
        final var path = toResourcePath(viewResource.getUri());
        return ResponseEntity.status(status)
                .setContentType(findContentType(path))
                .setMessageBody(findContent(path))
                .build()
                .toHttpResponse();
    }

    private Path toResourcePath(String uri) {
        try {
            final var classLoader = getClass().getClassLoader();
            final var url = classLoader.getResource("static" + uri);
            final var file = new File(url.getFile());
            return file.toPath();
        } catch (NullPointerException e) {
            throw HttpException.ofNotFound(e);
        }
    }

    private String findContent(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw HttpException.ofInternalServerError(e);
        }
    }

    private String findContentType(Path path) {
        try {
            return String.format("%s;charset=utf-8", Files.probeContentType(path));
        } catch (IOException e) {
            throw HttpException.ofInternalServerError(e);
        }
    }
}
