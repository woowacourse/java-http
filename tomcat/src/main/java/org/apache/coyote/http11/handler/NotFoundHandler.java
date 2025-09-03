package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class NotFoundHandler implements HttpRequestHandler {

    private static final String STATIC_DIR = "static";
    private static final String NOT_FOUND_PAGE = "/404.html";

    @Override
    public boolean canHandle(HttpRequest request) {
        return false;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream(STATIC_DIR + NOT_FOUND_PAGE);
        byte[] content = is.readAllBytes();
        is.close();

        HttpHeaders headers = HttpHeaders.fromLines(
                List.of(
                        "Content-Type: text/html;charset=utf-8",
                        "Content-Length: " + content.length
                )
        );

        return new HttpResponse(HttpStatus.NOT_FOUND, headers, HttpBody.from(content));
    }
}
