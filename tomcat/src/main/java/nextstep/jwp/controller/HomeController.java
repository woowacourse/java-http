package nextstep.jwp.controller;

import static org.apache.coyote.http11.header.ContentType.HTML;
import static org.apache.coyote.http11.header.ContentType.UTF_8;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.http.response.HttpStatus.OK;

import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class HomeController extends AbstractController {
    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE.getValue(), HTML.getValue(), UTF_8.getValue());
        httpResponse.setHttpStatus(OK);
        httpResponse.addHeader(contentType);
        httpResponse.setBody(DEFAULT_MESSAGE);
    }
}
