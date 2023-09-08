package nextstep.jwp.controller;

import org.apache.coyote.http.common.ContentType;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;

import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;

public class HomePageController extends RequestController {

    private static final String HOME_MESSAGE = "Hello world!";
    private static final String TARGET_URI = "/";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return TARGET_URI.equals(httpRequest.getRequestUri().getRequestUri());
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.changeStatusLine(StatusLine.from(StatusCode.OK));
        response.addHeader(CONTENT_TYPE, ContentType.HTML.getValue());
        response.changeBody(new HttpBody(HOME_MESSAGE));
    }
}
