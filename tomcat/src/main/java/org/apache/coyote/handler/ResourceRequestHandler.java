package org.apache.coyote.handler;

import org.apache.coyote.common.Headers;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.util.ResourceReader;

import java.util.Map;

import static org.apache.coyote.common.CharacterSet.UTF_8;
import static org.apache.coyote.common.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.common.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.common.HttpVersion.HTTP_1_1;
import static org.apache.coyote.response.HttpStatus.OK;

public class ResourceRequestHandler implements RequestHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String resourceBody = ResourceReader.read(httpRequest.requestLine().requestPath().source());
        final ResponseBody responseBody = new ResponseBody(resourceBody);

        final Headers responseHeader = new Headers(Map.of(
                CONTENT_TYPE.source(), httpRequest.mediaType().source() + ";" + UTF_8.source(),
                CONTENT_LENGTH.source(), String.valueOf(responseBody.length())
        ));

        return new HttpResponse(HTTP_1_1, OK, responseHeader, responseBody);
    }
}
