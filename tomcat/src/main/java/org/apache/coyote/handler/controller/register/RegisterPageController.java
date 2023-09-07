package org.apache.coyote.handler.controller.register;

import org.apache.coyote.handler.RequestController;
import org.apache.coyote.http.common.ContentType;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;

import static org.apache.coyote.handler.controller.Path.REGISTER;
import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;

public class RegisterPageController extends RequestController {

    private static final String TARGET_URI = "register";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isGetRequest() && httpRequest.containsRequestUri(TARGET_URI);
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
