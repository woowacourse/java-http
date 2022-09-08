package nextstep.jwp.controller;

import static org.apache.coyote.http11.HeaderField.CONTENT_LENGTH;
import static org.apache.coyote.http11.HeaderField.CONTENT_TYPE;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeaders;

public class HomePageController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {

        String requestUri = request.getRequestUri();
        ResponseBody body = ResponseBody.from(requestUri);
        final ResponseHeaders headers = ResponseHeaders.create()
                .addHeader(CONTENT_TYPE, ContentType.find(requestUri) + ";charset=utf-8 ")
                .addHeader(CONTENT_LENGTH, body.getBytesLength());
        return HttpResponse.create(HttpStatus.OK, headers, body);
    }
}
