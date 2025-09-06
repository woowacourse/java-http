package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.dto.ResourceResult;

public class HttpResponseBuilder {

    public HttpResponse build(final String path) throws IOException {
        var status = 200;
        var reason = "OK";
        var responseBody = findResponseBody(path);
        if (!responseBody.found()) {
            status = 404;
            reason = "NOT FOUND";
        }
        String mimeType = MimeType.fromPath(path);
        if (path.equals("/")) {
            mimeType = MimeType.HTML.mimeType();
        }

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", mimeType);
        headers.put("Content-Length", String.valueOf(responseBody.body().getBytes().length));

        return new HttpResponse(status, reason, headers, responseBody.body().getBytes());
    }

    private ResourceResult findResponseBody(String uri) throws IOException {
        if (uri.equals("/")) {
            return ResourceResult.found("Hello world!");
        }
        if (!uri.contains(".")) {
            uri += ".html";
        }
        final URL resource = getClass().getClassLoader().getResource("static/" + uri);
        if (resource == null) {
            return ResourceResult.notFound();
        }
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return ResourceResult.found(content);
    }
}
