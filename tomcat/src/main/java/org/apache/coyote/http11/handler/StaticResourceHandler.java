package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class StaticResourceHandler implements ResourceHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        String fileName = "static" + httpRequest.getPath();
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            fileName = "static/404.html";
            resource = getClass().getClassLoader().getResource(fileName);
            return createHttpResponse(StatusCode.BAD_REQUEST, resource, ContentType.from(fileName).getContentType());
        }
        return createHttpResponse(StatusCode.OK, resource, ContentType.from(fileName).getContentType());
    }

    private HttpResponse createHttpResponse(final StatusCode statusCode, final URL resource, final String contentType)
            throws IOException {
        final Path path = new File(resource.getPath()).toPath();
        final String body = new String(Files.readAllBytes(path));

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(body.getBytes().length));

        return new HttpResponse(
                "HTTP/1.1",
                statusCode,
                headers,
                body
        );
    }
}
