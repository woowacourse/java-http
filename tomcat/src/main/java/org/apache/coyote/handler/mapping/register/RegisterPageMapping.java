package org.apache.coyote.handler.mapping.register;

import org.apache.coyote.handler.AbstractController;
import org.apache.coyote.handler.mapping.HandlerMapping;
import org.apache.coyote.http.common.ContentType;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;

import java.io.IOException;

import static org.apache.coyote.handler.mapping.Path.REGISTER;
import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;

public class RegisterPageMapping extends AbstractController implements HandlerMapping {

    private static final String TARGET_URI = "register";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isGetRequest() && httpRequest.containsRequestUri(TARGET_URI);
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        return HttpResponse.builder()
                .statusLine(StatusLine.from(StatusCode.OK))
                .httpHeaders(CONTENT_TYPE, ContentType.HTML.getValue())
                .body(HttpBody.file(REGISTER.getPath()))
                .build();
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.changeStatusLine(StatusLine.from(StatusCode.OK));
        response.addHeader(CONTENT_TYPE, ContentType.HTML.getValue());
        response.changeBody(HttpBody.file(REGISTER.getPath()));
    }
}
