package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.dto.ResourceResult;

public class HttpResponseBuilder {

    private static final String ROOT = "/";

    public HttpResponse build(final String path) throws IOException {
        String formattedPath = formatPath(path);

        ResourceResult resourceResult = loadResource(formattedPath);
        HttpStatus status = findStatus(resourceResult);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", resourceResult.mimeType());
        headers.put("Content-Length", String.valueOf(resourceResult.body().getBytes().length));

        return new HttpResponse(status, headers, resourceResult.body().getBytes());
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

    private ResourceResult loadResource(String path) throws IOException {
        if (ROOT.equals(path)) {
            return ResourceResult.found(MimeType.HTML.mimeType(), "Hello world!");
        }

        final URL resource = getClass().getClassLoader().getResource("static/" + path);
        if (resource == null) {
            return ResourceResult.notFound();
        }
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        String mimeType = MimeType.fromPath(path);
        return ResourceResult.found(mimeType, content);
    }

    private HttpStatus findStatus(final ResourceResult resourceResult) {
        if (resourceResult.found()) {
            return HttpStatus.OK;
        }
        return HttpStatus.NOT_FOUND;
    }
}
