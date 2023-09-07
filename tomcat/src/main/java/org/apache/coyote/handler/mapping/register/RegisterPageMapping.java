package org.apache.coyote.handler.mapping.register;

import org.apache.coyote.handler.mapping.HandlerMapping;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;

import java.io.IOException;

import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;

public class RegisterPageMapping implements HandlerMapping {

    public static final String TARGET_URI = "register";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isGetRequest() && httpRequest.containsRequestUri(TARGET_URI);
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        return HttpResponse.builder()
                .statusLine(StatusLine.from(StatusCode.OK))
                .httpHeaders(CONTENT_TYPE, ContentType.HTML.getValue())
                .body(HttpBody.file("static/register.html"))
                .build();
    }
}
