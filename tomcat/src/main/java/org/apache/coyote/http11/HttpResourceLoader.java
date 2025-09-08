package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.dto.ResourceResult;

public class HttpResourceLoader {

    private static final String ROOT = "/";
    private static final String STATIC = "static/";

    public HttpResponse load(final String path) throws IOException {
        String formattedPath = formatPath(path);
        ResourceResult resourceResult = getResourceResult(formattedPath);
        HttpStatus status = findStatus(resourceResult);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", resourceResult.mimeType());
        headers.put("Content-Length", String.valueOf(resourceResult.body().length));

        return new HttpResponse(status, headers, resourceResult.body());
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
