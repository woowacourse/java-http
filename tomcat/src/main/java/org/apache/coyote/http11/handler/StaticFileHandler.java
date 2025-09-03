package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class StaticFileHandler implements HttpRequestHandler {

    private static final String STATIC_DIR = "static";
    private final HttpRequestHandler notFoundHandler;

    public StaticFileHandler(HttpRequestHandler notFoundHandler) {
        this.notFoundHandler = notFoundHandler;
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        String path = request.getRequestPath().getPath();
        return path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js");
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        String path = request.getRequestPath().getPath();
        InputStream is = getClass().getClassLoader().getResourceAsStream(STATIC_DIR + path);
        if (is == null) {
            return notFoundHandler.handle(request);
        }

        byte[] content = is.readAllBytes();
        is.close();

        HttpHeaders headers = HttpHeaders.fromLines(
                List.of(
                        "Content-Type: text/html;charset=utf-8",
                        "Content-Length: " + content.length
                )
        );

        return new HttpResponse(HttpStatus.OK, headers, HttpBody.from(content));
    }
}
