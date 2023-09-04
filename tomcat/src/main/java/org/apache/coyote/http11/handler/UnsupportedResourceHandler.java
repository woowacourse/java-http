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

public class UnsupportedResourceHandler implements ResourceHandler {

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return false;
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        String fileName = "static/404.html";
        URL resource = getClass().getClassLoader().getResource(fileName);
        final Path path = new File(resource.getPath()).toPath();
        final String body = new String(Files.readAllBytes(path));

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", ContentType.HTML.getContentType());
        headers.put("Content-Length", String.valueOf(body.getBytes().length));

        return new HttpResponse(
                "HTTP/1.1",
                StatusCode.BAD_REQUEST,
                headers,
                body
        );
    }
}
