package org.apache.coyote.http11.resourcehandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class StaticResourceHandler implements ResourceHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + httpRequest.getRequestUri());
        if (resource == null) {
            resource = getClass().getClassLoader().getResource("static/404.html");
            return createHttpResponse(StatusCode.BAD_REQUEST, resource);
        }
        return createHttpResponse(StatusCode.OK, resource);
    }

    private HttpResponse createHttpResponse(final StatusCode statusCode, final URL resource) throws IOException {
        final Path path = new File(resource.getPath()).toPath();
        final String body = new String(Files.readAllBytes(path));

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.getBytes().length));

        return new HttpResponse(
                "HTTP/1.1",
                statusCode,
                headers,
                body
        );
    }
}
