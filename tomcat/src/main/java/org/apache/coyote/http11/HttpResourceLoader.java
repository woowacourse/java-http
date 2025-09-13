package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.dto.ResourceResult;

public class HttpResourceLoader {

    private static final String ROOT = "/";
    private static final String STATIC = "static/";

    public void load(final String path, final HttpResponse response) throws IOException {
        String formattedPath = formatPath(path);
        ResourceResult resourceResult = getResourceResult(formattedPath);
        HttpStatus status = findStatus(resourceResult);

        response.addHeader("Content-Type", resourceResult.mimeType());
        response.setBody(resourceResult.body());
        response.setStatus(status);
    }

    private String formatPath(final String path) {
        if (path == null || path.isBlank() || path.equals(ROOT)) {
            return ROOT;
        }
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private ResourceResult getResourceResult(final String path) throws IOException {
        if (ROOT.equals(path)) {
            return ResourceResult.found(MimeType.HTML.mimeType(), "Hello world!".getBytes());
        }

        final URL resource = getClass().getClassLoader().getResource(STATIC + path);
        if (resource == null) {
            return ResourceResult.notFound();
        }
        byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());

        String mimeType = MimeType.fromPath(path);
        return ResourceResult.found(mimeType, bytes);
    }

    private HttpStatus findStatus(final ResourceResult resourceResult) {
        if (resourceResult.found()) {
            return HttpStatus.OK;
        }
        return HttpStatus.NOT_FOUND;
    }
}
