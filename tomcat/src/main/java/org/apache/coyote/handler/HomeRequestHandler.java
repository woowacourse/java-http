package org.apache.coyote.handler;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;

import static org.apache.coyote.common.HttpVersion.HTTP_1_1;
import static org.apache.coyote.common.MediaType.TEXT_HTML;
import static org.apache.coyote.response.HttpStatus.OK;

public class HomeRequestHandler implements RequestHandler {

    private static final String DEFAULT = "Hello world!";

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final ResponseBody responseBody = new ResponseBody(DEFAULT);

        return HttpResponse.builder()
                .setHttpVersion(HTTP_1_1)
                .setHttpStatus(OK)
                .setContentType(TEXT_HTML.value())
                .setContentLength(responseBody.length())
                .setResponseBody(responseBody)
                .build();
    }
}
