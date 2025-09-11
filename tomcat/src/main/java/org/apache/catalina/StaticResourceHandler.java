package org.apache.catalina;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;

public class StaticResourceHandler {

    public HttpResponse service(HttpRequest request) throws IOException {
        String path = request.getPath();
        System.out.println(path);
        byte[] body = getStaticResource(path);
        if (body == null) {
            body = getStaticResource("/404.html");
            return HttpResponse.notFound()
                    .contentType(ContentType.TEXT_HTML)
                    .contentLength(body.length)
                    .body(body)
                    .build();
        }
        String fileExtension = path.split("\\.")[1];
        return HttpResponse.ok()
                .contentType(ContentType.of(fileExtension))
                .contentLength(body.length)
                .body(body)
                .build();
    }

    private byte[] getStaticResource(String url) throws IOException {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("static" + url)) {
            if (inputStream == null) {
                return null;
            }
            return inputStream.readAllBytes();
        }
    }
}
