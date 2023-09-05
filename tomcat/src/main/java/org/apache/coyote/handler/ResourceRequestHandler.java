package org.apache.coyote.handler;

import org.apache.coyote.common.MediaType;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.util.ResourceReader;

import static org.apache.coyote.common.HttpVersion.HTTP_1_1;
import static org.apache.coyote.response.HttpStatus.OK;

public class ResourceRequestHandler implements RequestHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String requestPath = httpRequest.requestLine().requestPath().value();
        final String resourceBody = ResourceReader.read(requestPath);
        final ResponseBody responseBody = new ResponseBody(resourceBody);

        return HttpResponse.builder()
                .setHttpVersion(HTTP_1_1)
                .setContentType(MediaType.from(requestPath).value())
                .setHttpStatus(OK)
                .setResponseBody(responseBody)
                .setContentLength(resourceBody.length())
                .build();
    }
}
