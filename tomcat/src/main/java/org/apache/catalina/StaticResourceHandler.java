package org.apache.catalina;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticResourceHandler {

    public HttpResponse service(HttpRequest request) throws IOException {
        String path = request.getPath();
        Path staticResource = getStaticResource(path);
        if (staticResource == null) {
            byte[] body = Files.readAllBytes(getStaticResource("/404.html"));
            return HttpResponse.notFound()
                    .header("Content-Type", ContentType.TEXT_HTML.getMimeType())
                    .header("Content-Length", String.valueOf(body.length))
                    .body(body)
                    .build();
        }
        String fileExtension = path.split("\\.")[1];
        byte[] body = Files.readAllBytes(staticResource);
        return HttpResponse.ok()
                .header("Content-Type", ContentType.of(fileExtension).getMimeType())
                .header("Content-Length", String.valueOf(body.length))
                .body(body)
                .build();
    }

    private Path getStaticResource(String url) {
        URL resourceURL = getClass().getClassLoader().getResource("static" + url);
        if (resourceURL == null) {
            return null;
        }
        return Path.of(resourceURL.getFile());
    }
}
