package org.apache.coyote.handler;

import org.apache.coyote.common.Headers;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.ResponseBody;

import java.util.Map;

import static org.apache.coyote.common.CharacterSet.UTF_8;
import static org.apache.coyote.common.HeaderType.CONTENT_LENGTH;
import static org.apache.coyote.common.HeaderType.CONTENT_TYPE;
import static org.apache.coyote.common.MediaType.TEXT_HTML;

public class HomeRequestHandler implements RequestHandler {

    private static final String DEFAULT = "Hello world!";

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final ResponseBody responseBody = new ResponseBody(DEFAULT);
        final Headers responseHeader = new Headers(Map.of(
                CONTENT_TYPE.source(), TEXT_HTML.source() + ";" + UTF_8.source(),
                CONTENT_LENGTH.source(), String.valueOf(responseBody.length())
        ));

        return new HttpResponse(
                httpRequest.requestLine().httpVersion(),
                HttpStatus.OK,
                responseHeader,
                responseBody
        );
    }
}
