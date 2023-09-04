package org.apache.coyote.http11.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class DefaultResourceHandlers implements ResourceHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String body = "Hello world!";
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.getBytes().length));
        return new HttpResponse(
                "HTTP/1.1",
                StatusCode.OK,
                headers,
                body
        );
    }
}
