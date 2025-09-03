package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class HelloWorldHandler implements HttpRequestHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return "/".equals(request.getRequestPath());
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        byte[] content = "Hello world!".getBytes();
        HttpHeaders headers = HttpHeaders.fromLines(
                List.of(
                        "Content-Type: text/html;charset=utf-8",
                        "Content-Length: " + content.length
                )
        );
        return new HttpResponse(HttpStatus.OK, headers, HttpBody.from(content));
    }
}
