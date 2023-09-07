package org.apache.coyote.handler.mapping;

import org.apache.coyote.http.common.ContentType;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;

import java.io.IOException;

import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;

public class HomePageMapping implements HandlerMapping {

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isGetRequest() && "/".equals(httpRequest.getRequestUri().getRequestUri());
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        return HttpResponse.builder()
                .statusLine(StatusLine.from(StatusCode.OK))
                .httpHeaders(CONTENT_TYPE, ContentType.HTML.getValue())
                .body(new HttpBody("Hello world!"))
                .build();
    }
}
