package nextstep.jwp.controller;

import static org.apache.coyote.http11.http.HttpVersion.HTTP11;
import static org.apache.coyote.http11.header.ContentType.HTML;
import static org.apache.coyote.http11.header.ContentType.UTF_8;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.http.response.HttpStatus.OK;

import nextstep.jwp.controller.exception.NotFoundControllerException;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class HomeController implements Controller {
    private static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        if (httpRequest.isGetMethod()) {
            return doGet(httpRequest);
        }

        throw new NotFoundControllerException("해당하는 URL의 컨트롤러를 찾을 수 없습니다.");
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        final String body = DEFAULT_MESSAGE;
        final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE.getValue(), HTML.getValue(), UTF_8.getValue());
        final HttpHeader contentLength = HttpHeader.of(CONTENT_LENGTH.getValue(), String.valueOf(body.length()));

        return HttpResponse.of(HTTP11, OK, body, contentType, contentLength);
    }
}
