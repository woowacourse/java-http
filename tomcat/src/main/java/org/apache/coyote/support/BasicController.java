package org.apache.coyote.support;

import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.util.FileUtils;

public class BasicController {

    public HttpResponse execute(final HttpRequest httpRequest) {
        String body = FileUtils.readAllBytes(httpRequest.getUri().getValue());
        return HttpResponse.builder()
                .body(body)
                .version(httpRequest.getVersion())
                .status(HttpStatus.OK.getValue())
                .headers("Content-Type: " + httpRequest.getUri().getContentType().getValue(),
                        "Content-Length: " + body.getBytes().length)
                .build();
    }
}
